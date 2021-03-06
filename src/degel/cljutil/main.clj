;;; Copyright (c) 2012-2013 David Goldfarb. All rights reserved.
;;; Contact info: deg@degel.com
;;;
;;; The use and distribution terms for this software are covered by the Eclipse
;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;;; be found in the file epl-v10.html at the root of this distribution.
;;; By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.
;;;
;;; You must not remove this notice, or any other, from this software.


(ns degel.cljutil.main
  "Main namespace, here just for testing this library standalone."
  (:gen-class)
  (:require [degel.cljutil.utils :as utils]
            [degel.cljutil.devutils :as dev]
            [degel.cljutil.introspect :as introspect]))

(defn -main
  "Dummy function, to prove this lib is alive and, by the way, show what's in it."
  []
  (introspect/project-versions [["org.clojure" "clojure"]
                                ["org.clojure" "tools.trace"]
                                ["org.clojure" "tools.nrepl"]
                                ["com.cemerick" "pprng"]
                                ["midje" "midje"]
                                ["trptcolin" "versioneer"]
                                ["shoreleave" "shoreleave-remote-ring"]
                                ["shoreleave" "shoreleave-remote"]
                                ["degel" "degel-clojure-utils"]]))



