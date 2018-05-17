(ns miktau.nodes.db)
(def default-db
  {:nodes-sorted ""
   :nodes []
   :nodes-selected #{}
   :meta {:page :nodes}
   
   :total-nodes 0
   
   :cloud-selected #{}
   :calendar-selected {}})
