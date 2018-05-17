(ns miktau.edit-nodes.db)

(def default-db
  {:core-directory ""
   :meta {:page :edit-nodes}
   
   :nodes-temp-tags-to-delete #{}
   :nodes-temp-tags-to-add    ""
   
   :calendar-selected {}
   :nodes-selected #{}
   :cloud-selected #{}
   
   :cloud-can-select {}})
