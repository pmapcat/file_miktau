(ns miktau.nodes.db
  (:require [miktau.meta-db :as meta-db]))

(def default-db
  {:nodes-sorted ""
   :nodes []
   :nodes-selected #{}
   :meta (meta-db/set-page meta-db/meta-db :nodes)
   :page 1
   :page-size 15
   :total-pages 1
   
   :total-nodes 0

   :breadcrumbs {:tree-tag {}
                 :cloud-can-select {}
                 :show-all? false}
   
   :cloud-selected #{}})
