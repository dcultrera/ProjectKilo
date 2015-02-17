package cam.cl.kilo;

import cam.cl.kilo.lookup.AmznItemLookup;
import cam.cl.kilo.lookup.GoodReadsLookup;
import cam.cl.kilo.nlp.ItemInfo;
import cam.cl.kilo.nlp.Summarizer;
import cam.cl.kilo.nlp.Summary;
import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Path("/barcode")
public class RESTEasyBarcode {
	
	private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.encodeBase64String(baos.toByteArray());
    }

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response simpleResponse(
			@QueryParam("barcodeNo") String barcodeNo,
			@DefaultValue("ISBN") @QueryParam("barcodeType") String barcodeType) {

		String responseString;

		if (barcodeNo != null) {

			ItemInfo info = new ItemInfo();

			Thread tAmzn = new Thread(new AmznItemLookup(barcodeNo, barcodeType, info));
			Thread tGR = new Thread(new GoodReadsLookup(barcodeNo, barcodeType, info));

			tAmzn.start();
			tGR.start();

			while(tAmzn.isAlive() || tGR.isAlive());

//			System.out.println(info.getTitle());
//			for (String d : info.getDescriptions())
//				//System.out.println(d);
//			for (String a : info.getAuthors())
//				System.out.println(a);


            // Summarize text in ItemInfo and create a Summary object
            // If it fails, just return the full text of the first description
            Summarizer summarizer;
            String summarisedText = null;
            Summary summary;
            try {
                summarizer = new Summarizer(info);

                if (summarizer.getSummLength() != 0) {
                    summarisedText = summarizer.getSummResults();
                    System.out.println("Summarisation complete");
                } else {
                    summarisedText = summarizer.getText();
                    System.out.println("Empty summary");
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                //TODO: It would be better if it returned text from the Summary object...
                summarisedText = info.getDescriptions().firstElement();
            } finally {
                summary = new Summary(info, summarisedText);
                System.out.println(summary.getText());
            }

			try {
				responseString = toString(summary);
			} catch (IOException e) {
				e.printStackTrace();
				responseString = e.getMessage();
			}

        } else {
            responseString = "Missing barcode number.";
        }

		System.out.println(responseString);

        return Response.ok(responseString).build();
    }

    public static void main(String[] args) {
        RESTEasyBarcode test = new RESTEasyBarcode();
        test.simpleResponse("1407130226","ISBN");

    }
}
