(ns miktau.edit-nodes.db
  (:require [miktau.meta-db :as meta-db]))

(def default-db
  {:core-directory ""
   :meta (meta-db/set-page meta-db/meta-db :edit-nodes)
   :nodes [{:tags ["hello" "world"]}]
   :nodes-temp-tags-to-delete #{}
   :nodes-temp-tags-to-add    #{}
   
   :total-nodes 0
   
   :calendar-selected {}
   :nodes-selected #{}
   :cloud-selected #{}
   
   :cloud-can-select {}
   :cloud {}})
