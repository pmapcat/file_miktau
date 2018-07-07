(ns miktau.api-handler.events
  (:require [re-frame.core :as refe]
            [miktau.meta-db :as meta-db]
            [miktau.api-handler.query-builder :as query-builder]))

;; These are the events this NS should provide
;; 
;; [:api-handler/file-op :edit-nodes/got-app-data operation-name (:nodes-selected db) (:cloud-selected db)]
;; [:api-handler/submit-tagging :edit-nodes/got-app-data (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db)
;;  (:nodes-selected db) (:cloud-selected db)]

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
  "[:nodes/got-app-data \"\" (:nodes-selected db) (:cloud-selected db)]
   TESTED" 
  [{:keys [db]} [_ on-success sorted-str nodes-selected-set cloud-selected-set options]]
  (let [options (merge {:page-size 10 :page 1} options)]
    (if-let [query (query-builder/build-get-app-data (or sorted-str "") (or nodes-selected-set #{}) (or cloud-selected-set #{})  options nil)]
      {:db db :http-xhrio (query-builder/server-call-2 query [:api-handler/got-app-data on-success] [:error])}
      {:db db :fx-redirect [:error "Cannot build request on these params"]})))
(refe/reg-event-fx :api-handler/get-app-data get-app-data)

(defn got-app-data
  "TESTED"
  [{:keys [db]} [_ redirect-to response]]
  (if (empty? (:error response))
    {:db   (meta-db/set-loading db false) :fx-redirect [redirect-to response]}
    {:db   (meta-db/set-loading db false) :fx-redirect [:error (:error response)]}))
(refe/reg-event-fx :api-handler/got-app-data got-app-data)

(defn op-wrapper
  [db on-success-keyword apic]
  (if apic
    {:db (meta-db/set-loading db true) :http-xhrio  (query-builder/server-call-2 apic [:api-handler/got-app-data on-success-keyword] [:error])}
    {:db db :fx-redirect [:error "Cannot build request on these params"]}))

(defn file-operation
  "[:edit-nodes/on-fs-op-success <:symlinks | :default | :filebrowser> (:nodes-selected db) (:cloud-selected db)]
   TESTED"
  [{:keys [db]} [_ on-success action-keyword nodes-selected-set cloud-selected-set]]
  (op-wrapper db on-success
              (query-builder/build-bulk-operate-on-files action-keyword nodes-selected-set cloud-selected-set nil)))
(refe/reg-event-fx :api-handler/file-operation file-operation)


(defn build-update-records
  ":edit-nodes/success-redirect (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db) (:nodes-selected db) (:cloud-selected db)
   TESTED"
  [{:keys [db]} [_ on-success-keyword add-tags-set delete-tags-set nodes-selected-set cloud-selected-set]]
  (op-wrapper db on-success-keyword (query-builder/build-update-records add-tags-set delete-tags-set nodes-selected-set cloud-selected-set nil)))
(refe/reg-event-fx :api-handler/build-update-records build-update-records)

(defn swap-root-directory
  "[:api-handler/swap-root-directory :success-redirect \"some/wonderful/new/place/of/the/world/\"]
   cloud init empty page, because I decided to put it there"
  [{:keys [db]} [_  new-root-directory-str]]
  (op-wrapper db :cloud/init-page-empty (query-builder/build-swap-root-directory new-root-directory-str)))
(refe/reg-event-fx :api-handler/swap-root-directory swap-root-directory)

(defn open-single-file-in-default-program
  "[:api-handler/open-single-file-in-default-program :success-redirect \"some/wonderful/new/place/of/the/world/\"]
   identity, because there is nothing to change on the view side of things"
  [{:keys [db]} [_ fpath]]
  (op-wrapper db :identity (query-builder/build-open-single-file-in-default-program fpath)))
(refe/reg-event-fx :api-handler/open-single-file-in-default-program open-single-file-in-default-program)



(refe/reg-event-fx
 :api-handler/push-new-files-response-wrapper-hack
 (fn [{:keys [db]} [_ response]]
   (do
     (.log js/console (str response))
     {:db db
      :fx-redirect [:edit-nodes/init-page  (into #{} (:new-file-ids response)) #{}]})))
(comment
  (:nodes-selected @(refe/subscribe [:generic/test-db]))
  
  )
(defn push-new-files
  "[:api-handler/open-single-file-in-default-program :success-redirect \"some/wonderful/new/place/of/the/world/\"]
   In this case, response has not only Error. But also, the following struct
    type PushNewFilesRequest struct {
    	Error        error    `json:\"error\"`
    	NewFilePaths []string `json:\"new-file-paths\"`
    	NewFileIds   []int    `json:\"new-file-ids\"`
    }
   And we take this \"new-file-ids\" as an init parameter for :edit-nodes/init-page
   Read :api-handler/push-new-files-response-wrapper-hack for more details."
  [{:keys [db]} [_ fpath]]
  (op-wrapper db :api-handler/push-new-files-response-wrapper-hack (query-builder/build-push-new-files fpath)))
(refe/reg-event-fx :api-handler/push-new-files push-new-files)
