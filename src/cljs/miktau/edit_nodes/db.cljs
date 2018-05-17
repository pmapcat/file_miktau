(ns miktau.edit-nodes.db)

(def default-db
  {:core-directory ""
   :meta {:page :edit-nodes}
   :nodes [{:tags ["hello" "world"]}]
   
   :nodes-temp-tags-to-delete #{}
   :nodes-temp-tags-to-add    ""

   :total-nodes 0
   
   :calendar-selected {}
   :nodes-selected #{}
   :cloud-selected #{}
   
   :cloud-can-select {}})
