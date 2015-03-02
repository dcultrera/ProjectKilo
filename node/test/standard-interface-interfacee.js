/*
 * Copyright (C) 2015 Group Kilo (Cambridge Computer Lab)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
|
| type:         function
| author:       Josh Bambrick
|
*/
require("../lib/requirejs/config.js");

requirejs(["tools/standard-interface"], function (standardInterface) {
    // output whatever was sent
    standardInterface.read(function (input) {
        standardInterface.write(input);
    });
});