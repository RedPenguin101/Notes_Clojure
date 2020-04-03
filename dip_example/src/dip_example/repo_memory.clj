(ns dip-example.repo-memory
  (:require [dip-example.repo :as repo-interface]))

(def state (atom {}))

(defmethod repo-interface/load-entity :memory [repo entity-name]
  (assoc (entity-name @state) :fetched-from :memory-repo))

(defmethod repo-interface/update-entity :memory [repo entity]
  (swap! state assoc (:entity-name entity) entity))

(defmethod repo-interface/whole-repo :memory [repo-type]
  (assoc @state :fetched-from :memory-repo))
