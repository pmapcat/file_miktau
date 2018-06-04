(ns miktau.cloud.db
  (:require [miktau.meta-db :as meta-db]))

(def default-db
  {:meta (meta-db/set-page meta-db/meta-db :cloud)
   
   :filtering ""
   :date-now {}
   
   :total-nodes 0

   :breadcrumbs-show-all? false
   
   :cloud-selected #{}
   :cloud  {}
   :cloud-can-select {}
   :cloud-context  {}
   :patriarchs []})
