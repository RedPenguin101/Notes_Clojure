(ns dip-example.core
  (:require [dip-example.repo :as repo]))

(def app-config {:repo :memory}) ;; some sort of config loader

(def my-entity {:entity-name :my-entity :content "serious business things"})

(defn update-entity [entity]
  (repo/update-entity (:repo app-config) entity))

(defn load-entity [entity-name]
  (repo/load-entity (:repo app-config) entity-name))

(update-entity my-entity)
(load-entity :my-entity)
;; => {:entity-name :my-entity, :content "serious business things", :fetched-from :memory-repo}
(repo/whole-repo :memory)
;; => {:my-entity {:entity-name :my-entity, :content "serious business things"}, :fetched-from :memory-repo}

(def app-config {:repo :edn})
(update-entity my-entity)
(load-entity :my-entity)
;; => {:entity-name :my-entity, :content "serious business things", :fetched-from :edn-repo}
(repo/whole-repo :edn)
;; => {:my-entity {:entity-name :my-entity, :content "serious business things"}, :fetched-from :edn-repo}
