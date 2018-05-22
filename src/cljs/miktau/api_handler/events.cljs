(ns miktau.api-handler.events
  (:require [reframe.core :as refe]
            [miktau.api-handler.query-builder :as query-builder]))

;; These are the events this NS should provide
;; [:api-handler/get-app-data :nodes/got-app-data (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]
;; [:api-handler/file-op :edit-nodes/got-app-data operation-name (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]
;; [:api-handler/submit-tagging :edit-nodes/got-app-data (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db)
;;  (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]

;; First parameter, is where to redirect, if request is successful
;; It should also provide error handling:
;; * What happens if server returns error code?
;; * What happens if response contains :error in its results?

;; I should also include in generic get-app-data :nodes-selected parameter.

;; After this part is implemented. All query_builders must be retired.
;; And all server calls must be consolidated in this namespace
;; All tests should also be moved in here

(defn get-app-data
  "TESTED"
  [{:keys [db]} [_ on-success nodes-selected-set cloud-selected-set calendar-selected-dict]]
  {:db db
   :http-xhrio
   (query-builder/server-call
    {:url 
     :params  core-query}
    :edit-nodes/got-app-data :http-error)}
  {:db (meta-db/set-loading db false)
   :fx-redirect [:http-error "Cannot form query for given params"]})
(refe/reg-event-fx :edit-nodes/get-app-data get-app-data)


(comment
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
                  :request request}}))))




(comment
  :http-error
  [:api-handler/get-app-data :nodes/got-app-data (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]

  
  
  (defn file-operation-fx
  "action -> :symlinks | :default | :filebrowser"
  [{:keys [db]} [_ on-success action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [api-call (build-bulk-operate-on-files action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict nil)]
    {:http-xhrio (utils/server-call api-call [:fs-action/on-file-operation-finished on-success] :http-error)
     :db db}
    {:db db}))
  (refe/reg-event-fx :api-handler/file-op file-operation-fx)
  
  )

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db ;; (meta-db/set-loading db true)
   :http-xhrio
   (utils/server-call
    {:url "/api/get-app-data"
     :params
     {:modified (or (:calendar-selected db) {})
      :sorted   ""
      :tags     (or (into [] (sort (map str (map name (:cloud-selected db))))) [])}}
    :cloud/got-app-data :http-error)})


(refe/reg-event-fx :cloud/get-app-data get-app-data)

(defn file-operation-fx
  "action -> :symlinks | :default | :filebrowser"
  [{:keys [db]} [_ on-success action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [api-call (build-bulk-operate-on-files action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict nil)]
    {:http-xhrio (utils/server-call api-call [:fs-action/on-file-operation-finished on-success] :http-error)
     :db db}
    {:db db
     :http-error ["Cannot form request on given parameters"]}))

(refe/reg-event-fx :fs-action/file-op file-operation-fx)

(defn on-file-operation-finished
  "action -> :symlinks | :default-program | :filebrowser"
  [{:keys [db]} [_ response]]
  (if-not (empty? (:error response))
    {:db (meta-db/set-loading db false)
     :fx-redirect [:http-error (:error response)]}
    {:db (meta-db/set-loading db false)}))

(refe/reg-event-fx :fs-action/on-file-operation-finished on-file-operation-finished)





