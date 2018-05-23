(ns miktau.api-handler.query-builder
  (:require [miktau.tools :as utils]
            [ajax.core :as ajax]))

(defn server-call-2 [api-call on-success on-error]
  {:method :post
   :uri    (:url api-call)
   :response-format (ajax/json-response-format {:keywords? true})
   :format          (ajax/json-request-format)
   :timeout         8000
   :params  (:params api-call)
   :on-success on-success
   :on-failure on-error})

(defn build-core-query
  "TESTED"
  [sorted nodes-selected cloud-selected calendar-selected item-or]
  (cond
    (empty? nodes-selected)
    {:modified (or calendar-selected {})
     :sorted   (or sorted  "")
     :file-paths []
     :tags     (or (into [] (sort (map str (map name cloud-selected)))) [])}
    (contains? nodes-selected "*")
    {:modified (or calendar-selected {})
     :sorted   (or sorted "")
     :file-paths []
     :tags (into [] (sort (map str (map name cloud-selected))))}
    (not (empty? nodes-selected))
    {:modified {}
     :sorted   (or sorted "")
     :file-paths (or  (into [] (sort nodes-selected)) [])
     :tags []}
    :else item-or))

(defn build-bulk-operate-on-files
  "TESTED"
  [action nodes-selected cloud-selected calendar-selected or-else]
  (let [request (build-core-query "" nodes-selected cloud-selected calendar-selected nil)]
    (if (and (contains?  #{:symlinks :default-program :filebrowser} action) (not (nil? request)))
      {:url "/api/bulk-operate-on-files"
       :params {:action (str (name action)) :request request}}
      or-else)))

(defn build-get-app-data
  [sorted-str nodes-selected-set cloud-selected-set calendar-selected-dict or-else]
  (if-let [q (build-core-query sorted-str nodes-selected-set cloud-selected-set calendar-selected-dict or-else)]
    {:url "/api/get-app-data"
     :params q}
    or-else))

(defn build-update-records
  "TESTED"
  [tags-to-add tags-to-delete nodes-selected cloud-selected calendar-selected or-else]
  (let [request (build-core-query "" nodes-selected cloud-selected calendar-selected nil)
        tags-to-add    (into [] (sort (utils/find-all-tags-in-string tags-to-add)))
        tags-to-delete (into [] (sort (map (comp str name) tags-to-delete)))]
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

(defn build-check-is-live
  []
  {:url  "/api"
   :params {}})
