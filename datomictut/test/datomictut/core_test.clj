(ns datomictut.core-test
  (:require [midje.sweet :refer :all]
            [datomictut.core :refer :all]
            [datomic.api :as d]))

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://pet-owners-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/schema.edn")]
      (d/transact conn schema)
      conn)))

(fact "Adding one owner should allow us to find that owner"
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-pet-owner "John")
      (find-all-pet-owners))) => #{["John"]})

(fact
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-pet-owner "John")
      (add-pet-owner "Paul")
      (add-pet-owner "George")
      (find-all-pet-owners))) => #{["John"] ["Paul"] ["George"]})

(fact
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-pet-owner "John")
      (add-pet "Salt" "John")
      (find-pets-for-owner "John"))) => #{["Salt"]})

(fact
  (with-redefs [conn (create-empty-in-memory-db)]
    (do
      (add-pet-owner "John")
      (add-pet-owner "Paul")
      (add-pet-owner "George")
      (add-pet "Salt" "John")
      (add-pet "Pepper" "John")
      (add-pet "Martha" "Paul")
      (add-pet "Jet" "Paul")
      (find-pets-for-owner "Paul"))) => #{["Martha"] ["Jet"]})
