(ns miktau.api-handler.events
  (:require [reframe.core :as refe]
            [miktau.api-handler.query-builder :as query-builder]))

;; These are the events this NS should provide
;; 
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

;; Error handling:
;;  Error is placed inside `:meta`, as a data point `{:error "such and such error"}`
;;  Because it is easy to go back, (just "unerror" meta)
;; No way. What if the database is updated, and meta is somehow lost while request is running?
;; There will be no redirection. Or, I will have to implement 100% guarantee of meta to not to be lost...
;; Consulted the author of this awesome library. It seems, that they implement this behaviour.
;; Added this into work

(defn get-app-data
  "[:nodes/got-app-data \"\" (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]" 
  [{:keys [db]} [_ on-success sorted-str nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [query (query-builder/build-get-app-data (or sorted-str "") (or nodes-selected-set #{}) (or cloud-selected-set #{}) (or calendar-selected-dict {}) nil)]
    {:db db :http-xhrio (query-builder/server-call query [:api-handler/got-app-data on-success] [:error])}
    {:db db :fx-redirect [:error "Cannot build request on these params"]}))
(refe/reg-event-fx :api-handler/get-app-data get-app-data)

(defn got-app-data
  [{:keys [db]} [_ redirect-to response]]
  (if (empty? (:error response))
    {:db   db :fx-redirect [redirect-to response]}
    {:db   db :fx-redirect [:error (:error response)]}))
(refe/reg-event-fx :api-handler/got-app-data got-app-data)

(defn file-operation
  "[:edit-nodes/on-fs-op-success <:symlinks | :default | :filebrowser> (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)]"
  [{:keys [db]} [_ on-success action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [api-call (query-builder/build-bulk-operate-on-files action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict nil)]
    {:db db :http-xhrio (query-builder/server-call api-call [:api-handler/got-app-data on-success] [:error])}
    {:db db :fx-redirect [:error "Cannot build request on these params"]}))
(refe/reg-event-fx :api-handler/file-operation file-operation)

(defn build-update-records
  ":edit-nodes/success-redirect (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db) (:nodes-selected db) (:cloud-selected db) (:calendar-selected db)"
  [{:keys [db]} [_ on-success-keyword add-tags-string delete-tags-set nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [api-call (query-builder/build-update-records add-tags-string delete-tags-set nodes-selected-set cloud-selected-set calendar-selected-dict nil)]
    {:db db :http-xhrio (query-builder/server-call api-call [:api-handler/got-app-data on-success-keyword] [:error])}
    {:db db :fx-redirect [:error "Cannot build request on these params"]}))
(refe/reg-event-fx :api-handler/build-update-records build-update-records)
