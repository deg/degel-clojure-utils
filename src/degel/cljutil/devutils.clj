;;; Copyright (c) 2012, David Goldfarb. All rights reserved.
;;;
;;; Permission is hereby granted, free of charge, to any person obtaining
;;; a copy of this software and associated documentation
;;; files (the "Software"), to deal in the Software without restriction,
;;; including without limitation the rights to use, copy, modify, merge,
;;; publish, distribute, sublicense, and/or sell copies of the Software,
;;; and to permit persons to whom the Software is furnished to do so,
;;; subject to the following conditions:
;;;
;;; The above copyright notice and this permission notice shall be
;;; included in all copies or substantial portions of the Software.
;;;
;;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
;;; LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
;;; OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
;;; WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


(ns degel.cljutil.devutils
  (:require [clojure.string :as str]))

;;; Thanks to the many sources and code references I've looked at. In particular,
;;; portions of this code owe a debt to the following:
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
  `(-> (Throwable.) .getStackTrace first .toString unmangle))

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
