(ns miktau.nodes.db
  (:require [miktau.meta-db :as meta-db]))

(def default-db
  {:nodes-sorted ""
   :nodes []
   :nodes-selected #{}
   :meta (meta-db/set-page meta-db/meta-db :nodes)
   
   :total-nodes 0
   
   :cloud-selected #{}
   :calendar-selected {}})
