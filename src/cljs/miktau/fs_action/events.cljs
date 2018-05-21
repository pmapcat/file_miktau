(ns miktau.fs-action.events
  (:require
   [re-frame.core :as refe]
   [miktau.tools :as utils]
   [miktau.meta-db :as meta-db]))

(defn build-core-query-for-action
  "TESTED"
  [nodes-selected cloud-selected calendar-selected item-or]
  (cond (contains? nodes-selected "*")
        {:modified (or calendar-selected {})
         :sorted   ""
         :file-paths []
         :tags (into [] (sort (map str (map name cloud-selected))))}
        (not (empty? nodes-selected))
        {:modified {}
         :sorted   ""
         :file-paths (or   (into [] (sort nodes-selected)) [])
         :tags []}
        :else item-or))

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

(defn build-bulk-operate-on-files
  "TESTED"
  [action nodes-selected cloud-selected calendar-selected or-else]
  (let [request (build-core-query-for-action nodes-selected cloud-selected calendar-selected nil)]
    (if (and (contains?  #{:symlinks :default-program :filebrowser} action) (not (nil? request)))
      {:url "/api/bulk-operate-on-files"
       :params {:action (str (name action)) :request request}}
      or-else)))

(defn file-operation-fx
  "action -> :symlinks | :default | :filebrowser"
  [{:keys [db]} [_ on-success action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict]]
  (if-let [api-call (build-bulk-operate-on-files action-keyword nodes-selected-set cloud-selected-set calendar-selected-dict nil)]
    {:http-xhrio (utils/server-call api-call [:fs-action/on-file-operation-finished on-success] :http-error)
     :db db}
    {:db db}))
(refe/reg-event-fx :fs-action/file-op file-operation-fx)

(defn on-file-operation-finished
  "action -> :symlinks | :default-program | :filebrowser"
  [{:keys [db]} [_ response]]
  (if-not (empty? (:error response))
    {:db (meta-db/set-loading db false)
     :fx-redirect [:http-error (:error response)]}
    {:db (meta-db/set-loading db false)}))

(refe/reg-event-fx :fs-action/on-file-operation-finished on-file-operation-finished)




