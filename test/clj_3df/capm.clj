(ns clj-3df.capm
  (:require
   [clj-3df.attribute :as attribute]
   [clj-3df.core :refer [exec! create-debug-conn! create-db create-attribute
                         register-source query transact]]))

(def schema
  {:user      {:db/valueType :Number}
   :timestamp {:db/valueType :Number}
   :link/uri  {:db/valueType :String}

   ;; wip
   :apu/user (merge
              (attribute/of-type :Number)
              (attribute/input-semantics :db.semantics.cardinality/many)
              (attribute/tx-time))})

(def db (create-db schema))

(comment

  (def conn (debug-conn "ws://127.0.0.1:6262"))

  (exec! conn
    (create-attribute :apu/user (get schema :apu/user))
    (transact db [[:db/add 999999 :apu/user 3]]))

  (exec! conn
    (register-source
     [:user :timestamp :link/uri]
     {:JsonFile {:path "./data/capm/analytics_dump.json"}}))
  
  (exec! conn
    (query db "links" '[:find ?uri (count ?e)
                                 :where
                                 [?param :apu/user ?u]
                                 [?e :link/uri ?uri]
                                 [?e :user ?u]]))

  (exec! conn
    (query db "apu" '[:find (count ?e)
                               :where
                               [?param :apu/user ?u]
                               [?e :user ?u]]))

  (exec! conn
    (transact db [[:db/retract 999999 :apu/user 115]
                  [:db/add 999999 :apu/user 115]]))

  )
