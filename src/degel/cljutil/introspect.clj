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


(ns degel.cljutil.introspect
  (:require [shoreleave.middleware.rpc :as rpc]))


(defn get-project-version [group-id artifact]
  (let [path (str "META-INF/leiningen/" group-id "/" artifact "/project.clj")
        contents (some-> path clojure.java.io/resource slurp read-string)]
    (nth contents 2)))


(rpc/defremote project-version [group-id artifact]
  (get-project-version group-id artifact))
