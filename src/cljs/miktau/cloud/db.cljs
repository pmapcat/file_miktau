(ns miktau.cloud.db)

(def default-db
  {:meta {:page :cloud}
   :tree-tag {}
   :filtering ""
   :date-now {}
   
   
   :cloud-selected #{}
   :cloud  {}
   :cloud-can-select {}
   
   :calendar-selected {}
   :calendar   {:year {} :month {} :day   {}}
   :calendar-can-select {:year {} :month {} :day   {}}})
