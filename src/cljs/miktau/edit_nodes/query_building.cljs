(ns miktau.edit-nodes.query-building
  (:require [miktau.tools :as utils]))


(defn build-core-query-for-action
  "TESTED"
  [db item-or]
  (cond (contains?  (:nodes-selected db) "*")
        {:modified (or (:calendar-selected db) {})
         :sorted   ""
         :file-paths []
         :tags (into [] (sort (map str (map name (:cloud-selected db)))))}
        (not (empty? (:nodes-selected db)))
        {:modified {}
         :sorted   ""
         :file-paths (or   (into [] (sort (:nodes-selected db))) [])
         :tags []}
        :else item-or))
(defn build-core-query-for-retrieval
  "TESTED"
  [db]
  {:modified (or (:calendar-selected db) {})
   :sorted   (or (:nodes-sorted db) "")
   :tags     (or (into [] (sort (map str (map name (:cloud-selected db))))) [])})


(defn build-bulk-operate-on-files
  "TESTED"
  [db action or-else]
  (let [request (build-core-query-for-action db nil)]
    (if (and (contains?  #{:symlinks :default :filebrowser} action) (not (nil? request)))
      {:url "/api/bulk-operate-on-files"
       :params {:action (str (name action)) :request request}}
      or-else)))

(defn build-update-records
  "TESTED"
  [db or-else]
  (let [request (build-core-query-for-action db nil)
        tags-to-add    (into [] (sort (utils/find-all-tags-in-string (:nodes-temp-tags-to-add db))))
        tags-to-delete (into [] (sort (map (comp str name) (:nodes-temp-tags-to-delete db))))]
    (cond
      (nil? request)
      or-else
      (and (empty? tags-to-add) (empty? tags-to-delete))
      or-else
      :else
      {:url "/api/update-records"
       :params {:tags-to-add       tags-to-add
                :tags-to-delete    tags-to-delete
                :request request}})))
