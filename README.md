# degel-clojure-utils

A grab-bag of utility functions I'm using in other Clojure and
ClojureScript projects. As this continues to grow, I intend to slowly
organize and document this mess.  For now, dig in.

## Usage

For the latest version, include `[degel-clojure-utils "0.1.12"]` in your
`project.clj`.


When including this library in a ClojureScript project, note that you
also need to add the following to the ClojureScript compiler options
of each `:build` in your
[lein-cljsbuild](https://github.com/emezeske/lein-cljsbuild)
configuration:

```clojure
:libs [""]
```

(For more info, see [pprng's
README](https://github.com/cemerick/pprng) and [this ClojureScript
issue discussion](http://dev.clojure.org/jira/browse/CLJS-526).



## License

Copyright © 2013 David Goldfarb, deg@degel.com

Distributed under the Eclipse Public License, the same as Clojure.

The use and distribution terms for this software are covered by the
[Eclipse Public License
1.0](http://opensource.org/licenses/eclipse-1.0.php) which can be
found in the file epl-v10.html at the root of this distribution.  By
using this software in any fashion, you are agreeing to be bound by
the terms of this license.

You must not remove this notice, or any other, from this software.

