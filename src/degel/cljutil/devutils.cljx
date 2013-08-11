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


(ns degel.cljutil.devutils
  (:require [clojure.string :as str]
            [cljs.repl.browser]
            [cemerick.piggieback]))

;;; Thanks to the many sources and code references I've looked at as
;;; I've started learning Clojure. In particular, portions of this
;;; code owe a debt to the following:
;;;   http://stackoverflow.com/questions/2352020/debugging-in-clojure
;;;   https://gist.github.com/254110
;;;   https://groups.google.com/forum/?fromgroups=#!topic/clojure/bKBkInBCzf8
;;;   https://github.com/richhickey/clojure-contrib/blob/6dd033d9e12337f6630faa3d3f5c2e901a28c4f4/src/main/clojure/clojure/contrib/profile.clj
;;;   http://www.mail-archive.com/clojure@googlegroups.com/msg13018.html


;; [TODO] Fix to support multiple args, and maybe format string
(defmacro dbg
  ([x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))
  ([prefix x] `(let [x# ~x] (println ~prefix x#) x#)))

;; ===

(defmacro local-bindings
  "Produces a map of the names of local bindings to their values."
  []
  (let [symbols (map key @clojure.lang.Compiler/LOCAL_ENV)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(declare ^:dynamic *locals*)
(defn eval-with-locals
  "Evals a form with given locals. The locals should be a map of symbols to values."
  [locals form]
  (binding [*locals* locals]
    (eval
     `(let ~(vec (mapcat #(list % `(*locals* '~%)) (keys locals)))
        ~form))))

(defn dr-read
  [request-prompt request-exit]
  (let [input (clojure.main/repl-read request-prompt request-exit)]
    (if (= input '())
      request-exit
      input)))

(defmacro debug-repl
  "Starts a REPL with the local bindings available."
  []
  `(clojure.main/repl
    :prompt #(print "dr => ")
    :eval (partial eval-with-locals (local-bindings))
    :read dr-read))

;; ===

(defmacro bench
  "Times the execution of forms, discarding their output and returning a long in nanoseconds."
  ([& forms]
    `(let [start# (System/nanoTime)]
       ~@forms
       (- (System/nanoTime) start#))))

;; ===

(defn unmangle
  "Given the name of a class that implements a Clojure function, returns the function's
   name in Clojure. Note: If the true Clojure function name contains any underscores
   (a rare occurrence), the unmangled name will contain hyphens at those locations instead."
  [class-name]
  (.replace (str/replace class-name #"^(.+)\$(.+)\.invoke" "$1/$2") \_ \-))

(defmacro current-function-name []
  "Returns a string, the name of the current Clojure function."
  `(-> (Throwable.) .getStackTrace first .getClassName unmangle))

(defmacro current-function-desc []
  "Returns a string, the file, line number,and name of the current Clojure function."
  `(-> (Throwable.) .getStackTrace first  str unmangle))

(defmacro errmsg [e]
  `(str "Error in " (current-function-desc) ": " (.getMessage ~e)))

;; [TODO] This doesn't work. It looks like maybe catch is only understood in the context of try and
;;        this macro expands at the wrong time. Look at this again later.
#_(defmacro catch-print-err
  "Catch an error and simply print a one-line error description."
  [e]
  `(catch Exception ~e (println (dev/errmsg ~e))))

(defmacro nyi
  "Marker to flag unimplemented or incomplete functions."
  [msg]
  `(assert false (str "NYI " (current-function-name) ": " ~msg)))


(defn r15
  "Temporary (I hope) helper to bring in dev and debug tools to REPLs in Clojure 1.5"
  [for]
  (apply require clojure.main/repl-requires)
  (condp = for
    :server "Ready for server code"
    :client (do
               (require 'cljs.repl.browser)
               (println "Use in-ns to switch to cljs buffer's namespace")
               (cemerick.piggieback/cljs-repl
                :repl-env (doto (cljs.repl.browser/repl-env :port 9000)
                            cljs.repl/-setup)))
    "Unknown arg to r15"))
