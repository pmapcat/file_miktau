(ns miktau.edit-nodes.events
  (:require
   [re-frame.core :as refe]
   [miktau.tools :as utils]
   [miktau.edit-nodes.db :as miktau-db]
   [miktau.meta-db :as meta-db]
   [day8.re-frame.undo :refer [undoable]]))

(defn init
  "TODO: TEST
   params [_ nodes-selected-set cloud-selected-set ] are *nullable*"
  [{:keys [edit-nodes-warning]} [_ nodes-selected-set cloud-selected-set]]
  {:db
   (assoc miktau-db/default-db
          :meta
          (meta-db/set-loading-db (meta-db/set-page meta-db/meta-db :edit-nodes) true)
          :show-warning? (if (= "no-show" edit-nodes-warning) false true)
          :cloud-selected (or cloud-selected-set #{})
          :nodes-selected    (or  nodes-selected-set {}))
   :fx-redirect [:edit-nodes/get-app-data]})

(refe/reg-event-fx :edit-nodes/init-page
                   [(undoable "init edit nodes page") (refe/inject-cofx :generic/local-store :edit-nodes-warning)] init)

(defn get-app-data
  "TESTED"
  [{:keys [db]} _]
  {:db db
   :fx-redirect [:api-handler/get-app-data :edit-nodes/got-app-data "" (:nodes-selected db) (:cloud-selected db)]})
(refe/reg-event-fx :edit-nodes/get-app-data get-app-data)





(defn aknowledge-warning
  [{:keys [db]} _]
  {:db (assoc  db :show-warning? false)
   :generic/set-local-store! [:edit-nodes-warning "no-show"]})
(refe/reg-event-fx :edit-nodes/aknowledge-warning aknowledge-warning)

(defn got-app-data
  "TESTED"
  [db [_ response]]
  (->
   (meta-db/set-loading db false)
   (assoc :cloud-can-select (:cloud-can-select response))
   (assoc :nodes (:nodes response))
   (assoc :cloud (utils/filter-map-on-key (comp not utils/is-meta-tag?) (:cloud response)))
   (assoc :cloud-context (utils/filter-map-on-key (comp not utils/is-meta-tag?) (:cloud-context response)))
   (assoc :patriarchs  (:patriarchs response))
   (assoc :total-nodes (:total-nodes response))))
(refe/reg-event-db :edit-nodes/got-app-data got-app-data)

(defn generic-add-remove-op
  "TESTED"
  [db [_ on-set tag-item]]
  (let [node-temp-tags (into #{} (or (on-set db) #{}))]
    ;; no-op if selected
    (if (contains? (:cloud-selected db) tag-item) 
      db
      (assoc
       db
       on-set
       (cond
         (and (keyword? tag-item) (contains? node-temp-tags tag-item))
         (disj node-temp-tags tag-item)
         (keyword? tag-item)
         (conj node-temp-tags tag-item)
         :else
         node-temp-tags)
       :cloud
       (if (and (keyword? tag-item) (nil? (get-in db [:cloud tag-item])))
         (assoc (:cloud db) tag-item (:total-nodes db))
         (:cloud db))))))

(refe/reg-event-db
 :edit-nodes/tag-click
 (fn [db [_ tag-name]]
   (if (get-in db [:cloud-can-select tag-name])
     (generic-add-remove-op db [_ :nodes-temp-tags-to-delete tag-name])
     (generic-add-remove-op db [_ :nodes-temp-tags-to-add    tag-name]))))

(defn submit-tagging
  "TESTED"
  [{:keys [db]} [_ then-redirect?]]
  {:db  (assoc db :nodes-temp-tags-to-add #{}
               :nodes-temp-tags-to-delete #{})
   :fx-redirect
   [:api-handler/build-update-records
    (if then-redirect?
      :edit-nodes/on-after-submit-tagging
      :edit-nodes/get-app-data) (:nodes-temp-tags-to-add db) (:nodes-temp-tags-to-delete db)
    (:nodes-selected db) (:cloud-selected db)]})
(refe/reg-event-fx :edit-nodes/submit-tagging submit-tagging)

(defn on-after-submit-tagging
  "TESTED"
  [db _]
  {:db (assoc db :nodes-temp-tags-to-add #{} :nodes-temp-tags-to-delete #{})
   :fx-redirect  [:undo]})
(refe/reg-event-fx :edit-nodes/on-after-submit-tagging on-after-submit-tagging)


(defn cancel-tagging
  "TESTED"
  [db _]
  {:db (assoc db :nodes-temp-tags-to-add #{} :nodes-temp-tags-to-delete #{})
   :fx-redirect  [:undo]})
(refe/reg-event-fx :edit-nodes/cancel-tagging cancel-tagging)


(refe/reg-event-db
 :edit-nodes/clear
 (fn [db _]
   (assoc db :nodes-temp-tags-to-add #{} :nodes-temp-tags-to-delete #{})))
