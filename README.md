# g-cradle

**A little Graph data structure manipulation library written in Scala**

I encounter the *"We could store that in a Graph"* idea every month or so from a peer at the office or on my own doing a weekend project.  But I rarely see it go any further than that.  I see two issues that tend to get in the way:

1. The devil is in the details:  A Graph gives you a strong intuitive sense that it's a good solution.  But everything can point to everything.  So it's hard to agree or feel confident about how you end up structuring your data.
2. A Graph database is easily overkill:  When all you want is a little Graph it should be as easy as getting a List or a Set.  While you can use some Graph databases in a "lightweight" manner it can still feel like taking a sledgehammer to crack a nut. 

I thought a Graph would be in the Scala standard library by now but it's not.  So I wrote this to use while I wait.  It small, well-tested and has no dependencies.  It implements basic well-known graph operations along with some rather obscure things I find useful...

I'll flesh some basic usage patterns out here soon.  But if you're reading this and can't wait the test classes are good to look at for examples.

TODO:

- Switch to SBT.
- Move TimeTest to another project.
- Cleanup and eventually remove the GraphConverters class.

## License


    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2013 Charles Kreps

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.




