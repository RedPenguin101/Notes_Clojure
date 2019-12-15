(ns datomictut.core
  (:require [datomic.api :as d]))

(def conn nil)

(defn add-pet-owner [owner-name]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :owner/name owner-name}]))

(defn find-pet-owner-id [owner-name]
  (ffirst (d/q '[:find ?oid
                 :in $ ?owner-name
                 :where [?oid :owner/name ?owner-name]]
               (d/db conn)
               owner-name)))

(defn add-pet [pet-name owner-name]
  (let [pet-id (d/tempid :db.part/user)]
    @(d/transact conn [{:db/id pet-id
                        :pet/name pet-name}
                       {:db/id (find-pet-owner-id owner-name)
                        :owner/pets pet-id}])))

(defn find-all-pet-owners []
  (set (d/q '[:find ?owner-name
              :where [_ :owner/name ?owner-name]]
            (d/db conn))))

(defn find-pets-for-owner [owner-name]
  (d/q '[:find ?pet-name
         :in $ ?owner-name
         :where [?oid :owner/name ?owner-name]
                [?oid :owner/pets ?pet-id]
                [?pet-id :pet/name ?pet-name]]
       (d/db conn)
       owner-name))
