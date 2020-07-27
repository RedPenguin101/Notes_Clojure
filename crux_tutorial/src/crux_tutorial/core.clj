(ns crux-tutorial.core
  (:require [crux.api :as crux]))

"A crux node is the core component. Here we create an in-mem db"

(def crux
  (crux/start-node
    {:crux.node/topology                 :crux.standalone/topology
     :crux.node/kv-store                 "crux.kv.memdb/kv"
     :crux.standalone/event-log-dir      "data/eventlog-1"
     :crux.kv/db-dir                     "data/dbdir"
     :crux.standalone/event-log-kv-store "crux.kv.memdb/kv"}))

"Crux is a schemaless document db. it takes EDN data representing Entities, which must have a crux.db/id value.

Entities are Versioned."

(def manifest
  {:crux.db/id  :manifest
   :pilot-name  "Johanna"
   :id/rocket   "SB002-sol"
   :id/employee "22910x2"
   :badges      "SETUP"
   :cargo       ["stereo" "gold fish" "slippers" "secret note"]})

"submit edn to the db with submit-tx, and put. The other transcation operations are delete (deletes a version), cas (compare and swap), evict (remove an entity entirely)"

(crux/submit-tx crux [[:crux.tx/put manifest]])
;; => #:crux.tx{:tx-id 0, :tx-time #inst "2020-04-10T17:14:56.340-00:00"}

"and get Entities back with crux/entity"

(crux/entity (crux/db crux) :manifest)
;; =>
{:crux.db/id :manifest,   :pilot-name  "Johanna",
 :id/rocket  "SB002-sol", :id/employee "22910x2",
 :badges     "SETUP",
 :cargo      ["stereo" "gold fish" "slippers" "secret note"]}

"the put transaction can be passed time parameters which specify the time period which the document is valid for - both start and end valid times"

[:crux.tx/put doc valid-time-start valid-time-end]

"start defaults to transaction time, and end defaults to infinity. If the Entity is later updated, the previous version will have it's end time updated.

This means we can query an entity at a specific instant and see its state"

(crux/submit-tx crux
                [[:crux.tx/put
                  {:crux.db/id  :commodity/Pu
                   :common-name "Plutonium"
                   :type        :element/metal
                   :density     19.816
                   :radioactive true}]

                 [:crux.tx/put
                  {:crux.db/id  :commodity/N
                   :common-name "Nitrogen"
                   :type        :element/gas
                   :density     1.2506
                   :radioactive false}]

                 [:crux.tx/put
                  {:crux.db/id  :commodity/CH4
                   :common-name "Methane"
                   :type        :molecule/gas
                   :density     0.717
                   :radioactive false}]])
;; => #:crux.tx{:tx-id 1, :tx-time #inst "2020-04-10T17:30:19.493-00:00"}

(crux/submit-tx crux
                [[:crux.tx/put
                  {:crux.db/id :stock/Pu
                   :commod     :commodity/Pu
                   :weight-ton 21 }
                  #inst "2115-02-13T18"] ;; valid-time

                 [:crux.tx/put
                  {:crux.db/id :stock/Pu
                   :commod     :commodity/Pu
                   :weight-ton 23 }
                  #inst "2115-02-14T18"]

                 [:crux.tx/put
                  {:crux.db/id :stock/Pu
                   :commod     :commodity/Pu
                   :weight-ton 22.2 }
                  #inst "2115-02-15T18"]

                 [:crux.tx/put
                  {:crux.db/id :stock/Pu
                   :commod     :commodity/Pu
                   :weight-ton 24 }
                  #inst "2115-02-18T18"]

                 [:crux.tx/put
                  {:crux.db/id :stock/Pu
                   :commod     :commodity/Pu
                   :weight-ton 24.9 }
                  #inst "2115-02-19T18"]])

(crux/submit-tx crux
                [[:crux.tx/put
                  {:crux.db/id :stock/N
                   :commod     :commodity/N
                   :weight-ton 3 }
                  #inst "2115-02-13T18" ;; start valid-time
                  #inst "2115-02-19T18"] ;; end valid-time

                 [:crux.tx/put
                  {:crux.db/id :stock/CH4
                   :commod     :commodity/CH4
                   :weight-ton 92 }
                  #inst "2115-02-15T18"
                  #inst "2115-02-19T18"]])

(crux/entity (crux/db crux #inst "2115-02-14") :stock/Pu)
;; =>
{:crux.db/id :stock/Pu, :commod :commodity/Pu, :weight-ton 21}

(crux/entity (crux/db crux #inst "2115-02-18") :stock/Pu)
;; => {:crux.db/id :stock/Pu, :commod :commodity/Pu, :weight-ton 22.2}

"quick helper function..."

(defn easy-ingest [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs]
                         [:crux.tx/put doc]))))

"to update a document you put with the same id"

(crux/submit-tx
  crux
  [[:crux.tx/put
    (assoc manifest :badges ["SETUP" "PUT"])]])
;; => #:crux.tx{:tx-id 4, :tx-time #inst "2020-04-10T17:37:43.372-00:00"}

(crux/entity (crux/db crux) :manifest)
;; =>
{:crux.db/id :manifest, :pilot-name "Johanna", :id/rocket "SB002-sol", :id/employee "22910x2", :badges ["SETUP" "PUT"], :cargo ["stereo" "gold fish" "slippers" "secret note"]}

"(you can still get the old version)"

(crux/entity (crux/db crux #inst "2020-04-10T17:36:43.372-00:00") :manifest)
;; =>
{:crux.db/id :manifest, :pilot-name "Johanna", :id/rocket "SB002-sol", :id/employee "22910x2", :badges "SETUP", :cargo ["stereo" "gold fish" "slippers" "secret note"]}

"as well as entity, you can use traditional datalog queries to pull data"

(def mercury-data [{:crux.db/id  :commodity/Pu
                    :common-name "Plutonium"
                    :type        :element/metal
                    :density     19.816
                    :radioactive true}

                   {:crux.db/id  :commodity/N
                    :common-name "Nitrogen"
                    :type        :element/gas
                    :density     1.2506
                    :radioactive false}

                   {:crux.db/id  :commodity/CH4
                    :common-name "Methane"
                    :type        :molecule/gas
                    :density     0.717
                    :radioactive false}

                   {:crux.db/id  :commodity/Au
                    :common-name "Gold"
                    :type        :element/metal
                    :density     19.300
                    :radioactive false}

                   {:crux.db/id  :commodity/C
                    :common-name "Carbon"
                    :type        :element/non-metal
                    :density     2.267
                    :radioactive false}

                   {:crux.db/id  :commodity/borax
                    :common-name "Borax"
                    :IUPAC-name  "Sodium tetraborate decahydrate"
                    :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
                    :type        :mineral/solid
                    :appearance  "white solid"
                    :density     1.73
                    :radioactive false}])
(easy-ingest crux mercury-data)

(crux/q (crux/db crux)
        '{:find  [?name ?density]
          :where [[?e :type :element/metal]
                  [?e :common-name ?name]
                  [?e :density ?density]]})
;; => #{["Gold" 19.3] ["Plutonium" 19.816]}

"you can pass in args to the query - but note the differnt quotes."

(crux/q (crux/db crux)
        {:find  '[?name ?density]
         :where '[[?e :type t]
                  [?e :common-name ?name]
                  [?e :density ?density]]
         :args  [{'t :element/metal}]})
;; => #{["Gold" 19.3] ["Plutonium" 19.816]}


(defn filter-type [type]
  (crux/q (crux/db crux
                   {:find  '[name]
                    :where '[[e :type t]
                             [e :common-name name]]
                    :args  [{'t type}]})))

(filter-type :element/gas)
;; => #{["Nitrogen"]}

"COMPARE AND SWAP"

"Like put, but you pass in the old doc as well as the new doc, and the swap is only done if the old doc matches what's in the database. Nil can be provided if no previous version of the doc is expected in the DB."

(def saturn-data [{:crux.db/id   :gold-harmony
                   :company-name "Gold Harmony"
                   :seller?      true
                   :buyer?       false
                   :units/Au     10211
                   :credits      51}

                  {:crux.db/id   :tombaugh-resources
                   :company-name "Tombaugh Resources Ltd."
                   :seller?      true
                   :buyer?       false
                   :units/Pu     50
                   :units/N      3
                   :units/CH4    92
                   :credits      51}

                  {:crux.db/id   :encompass-trade
                   :company-name "Encompass Trade"
                   :seller?      true
                   :buyer?       true
                   :units/Au     10
                   :units/Pu     5
                   :units/CH4    211
                   :credits      1002}

                  {:crux.db/id   :blue-energy
                   :seller?      false
                   :buyer?       true
                   :company-name "Blue Energy"
                   :credits      1000}])

(easy-ingest crux saturn-data)

"the above are traders. We need to make sure buy and sell transactions only happens if the buyer has enough credits, or the seller has enough stock. We can use CAS to implement atomicity"

(defn stock-check [company-id item]
  {:result (crux/q (crux/db crux)
                   {:find  '[name funds stock]
                    :where ['[e :company-name name]
                            '[e :credits funds]
                            ['e item 'stock]]
                    :args  [{'e company-id}]})
   :item   item})

(stock-check :encompass-trade :units/Au)

(defn format-stock-check
  [{:keys [result item] :as stock-check}]
  (for [[name funds commod] result]
    (str "Name: " name ", Funds: " funds ", " item " " commod)))

(-> (stock-check :encompass-trade :units/Au)
    format-stock-check)
;; => ("Name: Encompass Trade, Funds: 1002, :units/Au 10")

"this one will go through"

(crux/submit-tx
  crux
  [[:crux.tx/cas
    ;; Old doc
    {:crux.db/id   :blue-energy
     :seller?      false
     :buyer?       true
     :company-name "Blue Energy"
     :credits      1000}
    ;; New doc
    {:crux.db/id   :blue-energy
     :seller?      false
     :buyer?       true
     :company-name "Blue Energy"
     :credits      900
     :units/CH4    10}]

   [:crux.tx/cas
    ;; Old doc
    {:crux.db/id   :tombaugh-resources
     :company-name "Tombaugh Resources Ltd."
     :seller?      true
     :buyer?       false
     :units/Pu     50
     :units/N      3
     :units/CH4    92
     :credits      51}
    ;; New doc
    {:crux.db/id   :tombaugh-resources
     :company-name "Tombaugh Resources Ltd."
     :seller?      true
     :buyer?       false
     :units/Pu     50
     :units/N      3
     :units/CH4    82
     :credits      151}]])

(format-stock-check (stock-check :tombaugh-resources :units/CH4))
;; => ("Name: Tombaugh Resources Ltd., Funds: 151, :units/CH4 82")

(format-stock-check (stock-check :blue-energy :units/CH4))
;; => ("Name: Blue Energy, Funds: 900, :units/CH4 10")


"this one will not, because the old doc doesn't match the latest state in the db"
(format-stock-check (stock-check :gold-harmony :units/Au))
;; => ("Name: Gold Harmony, Funds: 51, :units/Au 10211")
(format-stock-check (stock-check :encompass-trade :units/Au))
;; => ("Name: Encompass Trade, Funds: 1002, :units/Au 10")

(crux/submit-tx
  crux
  [[:crux.tx/cas
    ;; Old doc
    {:crux.db/id   :gold-harmony
     :company-name "Gold Harmony"
     :seller?      true
     :buyer?       false
     :units/Au     10211
     :credits      51}
    ;; New doc
    {:crux.db/id   :gold-harmony
     :company-name "Gold Harmony"
     :seller?      true
     :buyer?       false
     :units/Au     211
     :credits      51}]

   [:crux.tx/cas
    ;; Old doc
    {:crux.db/id   :encompass-trade
     :company-name "Encompass Trade"
     :seller?      true
     :buyer?       true
     :units/Au     10
     :units/Pu     5
     :units/CH4    211
     :credits      100002} ;; doesn't match db state
    ;; New doc
    {:crux.db/id   :encompass-trade
     :company-name "Encompass Trade"
     :seller?      true
     :buyer?       true
     :units/Au     10010
     :units/Pu     5
     :units/CH4    211
     :credits      1002}]])

(format-stock-check (stock-check :gold-harmony :units/Au))
;; => ("Name: Gold Harmony, Funds: 51, :units/Au 10211")
(format-stock-check (stock-check :encompass-trade :units/Au))
;; => ("Name: Encompass Trade, Funds: 1002, :units/Au 10")

"no change"

"DELETING"

'[:crux.tx/delete eid valid-time-start valid-time-end]

(crux/submit-tx crux
                [[:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients    [:encompass-trade]}
                  #inst "2110-01-01T09"
                  #inst "2111-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients    [:encompass-trade :blue-energy]}
                  #inst "2111-01-01T09"
                  #inst "2113-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients    [:blue-energy]}
                  #inst "2113-01-01T09"
                  #inst "2114-01-01T09"]

                 [:crux.tx/put {:crux.db/id :kaarlang/clients
                                :clients    [:blue-energy :gold-harmony :tombaugh-resources]}
                  #inst "2114-01-01T09"
                  #inst "2115-01-01T09"]])

(crux/history-ascending
  (crux/db crux)
  (crux/new-snapshot (crux/db crux #inst "2116-01-01T09"))
  :kaarlang/clients)
;; =>
({:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "5ec42ea653288e01e1a9d7d2068b4658416177e0",
  :crux.db/valid-time   #inst "2110-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:encompass-trade]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "cd71551fe21219db59067ce7483370fdebaae8b0",
  :crux.db/valid-time   #inst "2111-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:encompass-trade :blue-energy]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "000e5b775b55d06f0bddc77d736184284aa1e4e9",
  :crux.db/valid-time   #inst "2113-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:blue-energy]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "d4bca6c78409d9d40ee42319a8aec32bffad9030",
  :crux.db/valid-time   #inst "2114-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:blue-energy :gold-harmony
                                      :tombaugh-resources]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "0000000000000000000000000000000000000000",
  :crux.db/valid-time   #inst "2115-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          nil})

(crux/submit-tx crux
                [[:crux.tx/delete :kaarlang/clients #inst "2113-06-01" #inst "2116-01-01"]]);; => #:crux.tx{:tx-id 10, :tx-time #inst "2020-04-10T18:22:42.540-00:00"}

(crux/history-ascending
  (crux/db crux)
  (crux/new-snapshot (crux/db crux #inst "2116-01-01T09"))
  :kaarlang/clients)
;; =>
({:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "5ec42ea653288e01e1a9d7d2068b4658416177e0",
  :crux.db/valid-time   #inst "2110-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:encompass-trade]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "cd71551fe21219db59067ce7483370fdebaae8b0",
  :crux.db/valid-time   #inst "2111-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9, :crux.db/doc {:crux.db/id :kaarlang/clients, :clients [:encompass-trade :blue-energy]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "000e5b775b55d06f0bddc77d736184284aa1e4e9",
  :crux.db/valid-time   #inst "2113-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          {:crux.db/id :kaarlang/clients,
                         :clients    [:blue-energy]}}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "0000000000000000000000000000000000000000",
  :crux.db/valid-time   #inst "2113-06-01T00:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:22:42.540-00:00",
  :crux.tx/tx-id        10,
  :crux.db/doc          nil}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "0000000000000000000000000000000000000000",
  :crux.db/valid-time   #inst "2114-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:22:42.540-00:00",
  :crux.tx/tx-id        10,
  :crux.db/doc          nil}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "0000000000000000000000000000000000000000",
  :crux.db/valid-time   #inst "2115-01-01T09:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:22:42.540-00:00",
  :crux.tx/tx-id        10,
  :crux.db/doc          nil}
 {:crux.db/id           "0ff5010a7da6b11cdb7bbcbd362befcea6beccdf",
  :crux.db/content-hash "0000000000000000000000000000000000000000",
  :crux.db/valid-time   #inst "2116-01-01T00:00:00.000-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:17:18.448-00:00",
  :crux.tx/tx-id        9,
  :crux.db/doc          nil})

"EVICT"

(crux/submit-tx crux
                [[:crux.tx/put
                  {:crux.db/id    :person/kaarlang
                   :full-name     "Kaarlang"
                   :origin-planet "Mars"
                   :identity-tag  :KA01299242093
                   :DOB           #inst "2040-11-23"}]

                 [:crux.tx/put
                  {:crux.db/id    :person/ilex
                   :full-name     "Ilex Jefferson"
                   :origin-planet "Venus"
                   :identity-tag  :IJ01222212454
                   :DOB           #inst "2061-02-17"}]

                 [:crux.tx/put
                  {:crux.db/id   :person/thadd
                   :full-name    "Thad Christover"
                   :origin-moon  "Titan"
                   :identity-tag :IJ01222212454
                   :DOB          #inst "2101-01-01"}]

                 [:crux.tx/put
                  {:crux.db/id    :person/johanna
                   :full-name     "Johanna"
                   :origin-planet "Earth"
                   :identity-tag  :JA012992129120
                   :DOB           #inst "2090-12-07"}]])

(defn full-query
  [node]
  (crux/q
    (crux/db node)
    '{:find          [id]
      :where         [[e :crux.db/id id]]
      :full-results? true}))

(full-query crux)
;; =>
#{[{:crux.db/id :person/ilex, :full-name "Ilex Jefferson", :origin-planet "Venus", :identity-tag :IJ01222212454, :DOB #inst "2061-02-17T00:00:00.000-00:00"}] [{:crux.db/id :encompass-trade, :company-name "Encompass Trade", :seller? true, :buyer? true, :units/Au 10, :units/Pu 5, :units/CH4 211, :credits 1002}] [{:crux.db/id :gold-harmony, :company-name "Gold Harmony", :seller? true, :buyer? false, :units/Au 10211, :credits 51}] [{:crux.db/id :commodity/CH4, :common-name "Methane", :type :molecule/gas, :density 0.717, :radioactive false}] [{:crux.db/id :person/thadd, :full-name "Thad Christover", :origin-moon "Titan", :identity-tag :IJ01222212454, :DOB #inst "2101-01-01T00:00:00.000-00:00"}] [{:crux.db/id :manifest, :pilot-name "Johanna", :id/rocket "SB002-sol", :id/employee "22910x2", :badges ["SETUP" "PUT"], :cargo ["stereo" "gold fish" "slippers" "secret note"]}] [{:crux.db/id :commodity/Pu, :common-name "Plutonium", :type :element/metal, :density 19.816, :radioactive true}] [{:crux.db/id :person/kaarlang, :full-name "Kaarlang", :origin-planet "Mars", :identity-tag :KA01299242093, :DOB #inst "2040-11-23T00:00:00.000-00:00"}] [{:crux.db/id :commodity/Au, :common-name "Gold", :type :element/metal, :density 19.3, :radioactive false}] [{:crux.db/id :commodity/borax, :common-name "Borax", :IUPAC-name "Sodium tetraborate decahydrate", :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"], :type :mineral/solid, :appearance "white solid", :density 1.73, :radioactive false}] [{:crux.db/id :commodity/C, :common-name "Carbon", :type :element/non-metal, :density 2.267, :radioactive false}] [{:crux.db/id :tombaugh-resources, :company-name "Tombaugh Resources Ltd.", :seller? true, :buyer? false, :units/Pu 50, :units/N 3, :units/CH4 82, :credits 151}] [{:crux.db/id :blue-energy, :seller? false, :buyer? true, :company-name "Blue Energy", :credits 900, :units/CH4 10}] [{:crux.db/id :commodity/N, :common-name "Nitrogen", :type :element/gas, :density 1.2506, :radioactive false}] [{:crux.db/id :person/johanna, :full-name "Johanna", :origin-planet "Earth", :identity-tag :JA012992129120, :DOB #inst "2090-12-07T00:00:00.000-00:00"}]}


(crux/submit-tx crux [[:crux.tx/evict :person/kaarlang]])
;; => #:crux.tx{:tx-id 12, :tx-time #inst "2020-04-10T18:31:36.877-00:00"}

(full-query crux)
;; =>
#{[{:crux.db/id :person/ilex, :full-name "Ilex Jefferson", :origin-planet "Venus", :identity-tag :IJ01222212454, :DOB #inst "2061-02-17T00:00:00.000-00:00"}] [{:crux.db/id :encompass-trade, :company-name "Encompass Trade", :seller? true, :buyer? true, :units/Au 10, :units/Pu 5, :units/CH4 211, :credits 1002}] [{:crux.db/id :gold-harmony, :company-name "Gold Harmony", :seller? true, :buyer? false, :units/Au 10211, :credits 51}] [{:crux.db/id :commodity/CH4, :common-name "Methane", :type :molecule/gas, :density 0.717, :radioactive false}] [{:crux.db/id :person/thadd, :full-name "Thad Christover", :origin-moon "Titan", :identity-tag :IJ01222212454, :DOB #inst "2101-01-01T00:00:00.000-00:00"}] [{:crux.db/id :manifest, :pilot-name "Johanna", :id/rocket "SB002-sol", :id/employee "22910x2", :badges ["SETUP" "PUT"], :cargo ["stereo" "gold fish" "slippers" "secret note"]}] [{:crux.db/id :commodity/Pu, :common-name "Plutonium", :type :element/metal, :density 19.816, :radioactive true}] [{:crux.db/id :commodity/Au, :common-name "Gold", :type :element/metal, :density 19.3, :radioactive false}] [{:crux.db/id :commodity/borax, :common-name "Borax", :IUPAC-name "Sodium tetraborate decahydrate", :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"], :type :mineral/solid, :appearance "white solid", :density 1.73, :radioactive false}] [{:crux.db/id :commodity/C, :common-name "Carbon", :type :element/non-metal, :density 2.267, :radioactive false}] [{:crux.db/id :tombaugh-resources, :company-name "Tombaugh Resources Ltd.", :seller? true, :buyer? false, :units/Pu 50, :units/N 3, :units/CH4 82, :credits 151}] [{:crux.db/id :blue-energy, :seller? false, :buyer? true, :company-name "Blue Energy", :credits 900, :units/CH4 10}] [{:crux.db/id :commodity/N, :common-name "Nitrogen", :type :element/gas, :density 1.2506, :radioactive false}] [{:crux.db/id :person/johanna, :full-name "Johanna", :origin-planet "Earth", :identity-tag :JA012992129120, :DOB #inst "2090-12-07T00:00:00.000-00:00"}]}

(crux/history-descending (crux/db crux)
                         (crux/new-snapshot (crux/db crux))
                         :person/kaarlang)
;; =>
({:crux.db/id           "efe634523d6867a3c6e4089074adf29b07b45f43",
  :crux.db/content-hash "c3ad3191fff06083fedf3640b625566c02033a6b",
  :crux.db/valid-time   #inst "2020-04-10T18:27:41.732-00:00",
  :crux.tx/tx-time      #inst "2020-04-10T18:27:41.732-00:00",
  :crux.tx/tx-id        11,
  :crux.db/doc
  #:crux.db{:id       #crux/id "efe634523d6867a3c6e4089074adf29b07b45f43",
            :evicted? true}})
