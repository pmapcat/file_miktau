(ns miktau.api-handler.query-builder
  (:require [ajax.core :as ajax]))

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
  [sorted nodes-selected cloud-selected item-or]
  (cond
    (empty? nodes-selected)
    {:sorted   (or sorted  "")
     :ids []
     :tags     (or (into [] (sort (map str (map name cloud-selected)))) [])}
    (contains? nodes-selected "*")
    {:sorted   (or sorted "")
     :ids []
     :tags (into [] (sort (map str (map name cloud-selected))))}
    (not (empty? nodes-selected))
    {:sorted   (or sorted "")
     :ids (or  (into [] (sort nodes-selected)) [])
     :tags []}
    :else item-or))

(defn build-bulk-operate-on-files
  "TESTED"
  [action nodes-selected cloud-selected or-else]
  (let [request (build-core-query "" nodes-selected cloud-selected nil)]
    (if (and (contains?  #{:symlinks :default :filebrowser} action) (not (nil? request)))
      {:url "/api/bulk-operate-on-files"
       :params {:action (str (name action)) :request request}}
      or-else)))

(defn build-get-app-data
  [sorted-str nodes-selected-set cloud-selected-set options or-else]
  (if-let [q (build-core-query sorted-str nodes-selected-set cloud-selected-set or-else)]
    {:url "/api/get-app-data"
     :params (merge q options)}
    or-else))

(defn build-update-records
  "TESTED"
  [tags-to-add tags-to-delete nodes-selected cloud-selected or-else]
  (let [request (build-core-query "" nodes-selected cloud-selected nil)
        tags-to-add    (into [] (sort (map (comp str name) tags-to-add)))
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
