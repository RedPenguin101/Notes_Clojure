(ns dip-example.repo-edn
  (:require [dip-example.repo :as repo-interface]
            [clojure.edn :as edn]))

(def filename "resources/data.edn")

(defn read-repo [filename]
  (edn/read-string (slurp filename)))

(defn write-repo [filename repo]
  (spit filename (prn-str repo)))

(defmethod repo-interface/load-entity :edn [repo entity-name]
  (assoc (entity-name (read-repo filename)) :fetched-from :edn-repo))

(defmethod repo-interface/update-entity :edn [repo entity]
  (write-repo filename
              (assoc (read-repo filename)
                     (:entity-name entity) entity)))

(defmethod repo-interface/whole-repo :edn [repo-type]
  (assoc (read-repo filename) :fetched-from :edn-repo))

(def my-entity {:entity-name :my-entity :content "serious business things"})
