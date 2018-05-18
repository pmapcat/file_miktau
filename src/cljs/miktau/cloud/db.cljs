(ns miktau.cloud.db
  (:require [miktau.meta-db :as meta-db]))

(def default-db
  {:meta (meta-db/set-page meta-db/meta-db :cloud)
   :tree-tag {}
   :filtering ""
   :date-now {}
   
   
   :cloud-selected #{}
   :cloud  {}
   :cloud-can-select {}
   
   :calendar-selected {}
   :calendar   {:year {} :month {} :day   {}}
   :calendar-can-select {:year {} :month {} :day   {}}})
