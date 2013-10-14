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
  (:require [trptcolin.versioneer.core :as version]
            [shoreleave.middleware.rpc :as rpc]
            [degel.cljutil.devutils :as dev]))


(rpc/defremote project-versions
  "Return version strings for one or more artifacts in the
  project. Called with a vector of 2-vectors, each being the group-id
  and artifact of one component."
  [projects]
  (into [] (map (fn [[group-id artifact]]
                  [group-id artifact (version/get-version group-id artifact)])
                projects)))
