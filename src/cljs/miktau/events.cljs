(ns miktau.events
  (:require
   [day8.re-frame.http-fx]
   [miktau.utils :as utils]
   [clojure.string :as clojure-string]
   [re-frame.core :as refe]))

(def demo-db
  {:loading? true
    :filtering ""
    
    :nodes-sorted "-name"
    :core-directory ":test:"
    :date-now {:year 2016 :month 7 :day 21}
    
    :nodes [{:id 0, :name "blab.mp4" :file-path "/home/mik/this_must_be_it/" :tags []
             :modified {:year 2016 :month 7 :day 21}}]
    :nodes-selected #{"*"}
    :nodes-temp-tags-to-delete #{}
    :nodes-temp-tags-to-add    #{}
    
    :cloud-selected #{:blab}
    :cloud  {:VolutPatem {:blab 43 :blip 27 :blop 12}}
    :cloud-can-select {:blip true :blop true}
    
    :calendar-selected {:year  2018  :day 23 :month 11}
    :calendar   {:year {:2018 12 :2017 13 :2016 12}
                 :month {:12 1 :13 1 :14 2}
                 :day   {:1 3 :2 3 :3 4}}
    :calendar-can-select {:year {:2018 2}
                          :month {:11 3}
                          :day   {:9 3}}})

(refe/reg-event-fx
 :init
 (fn [_ _]
   {:db (assoc demo-db :loading? true) 
    :http-xhrio
    (utils/with-http-xhrio
      {:method :post
       :uri    "/api/get-app-data"
       :params  {}
       :on-success [:got-app-data]
       :on-failure [:http-error]})}))

(refe/reg-event-db
 :set-nodes-temp-tags-to-delete
 (fn [db _]
   (assoc
    db
    :nodes-temp-tags-to-delete
    (let [all-selected? (=  (first (db :nodes-selected)) "*")]
      (if all-selected?
        (into #{} (map str (map name (keys (db :cloud-can-select)))))
        (into #{}
              (map
               str
               (into
                []
                (flatten
                 (for [item (db :nodes)]
                   (if (contains? (db :nodes-selected) (item :file-path))
                     (:tags item)
                     nil)))))))))))

(refe/reg-event-fx
 :http-error
 (fn [{:keys [db]} [_ response]]
   {:db (assoc db :loading? false)
    :log!  (str response)}))

(refe/reg-event-fx
 :get-app-data
 (fn [{:keys [db]} _]
   {:db (assoc  db :loading? true) 
    :http-xhrio
    (utils/with-http-xhrio
      {:method :post
       :uri    "/api/get-app-data"
       :params  {}
       :on-success [:got-app-data]
       :on-failure [:http-error]})}))

(defn got-app-data
  [{:keys [db]} [_ response]]
  (assoc
   (merge
    db
    response)
   :loading? false))


(refe/reg-event-db
 :got-app-data
 got-app-data)

(comment
  (println (str dodo))
  (refe/dispatch [:get-app-data])
  (println (first (:nodes  heho)))
  (:nodes-sorted heho)
  (:cloud heho)
  (:calendar heho)
  (:calendar-can-select heho)
  (:core-directory heho)
  (keys heho))

(refe/reg-event-db
 :back
 (fn [db _]
   (.log js/console "registered <back> event")
   db))

(defn filtering
  "TESTED"
  [db [_ data]]
  (assoc db :filtering (str data)))
(refe/reg-event-db :filtering filtering)

(defn clear
  "TESTED"
  [db _]
  (assoc db :filtering ""))
(refe/reg-event-db :clear clear)
(defn click-on-calendar-item
  "TESTED"
  [db [_ group key-name]]
  (if (=  group "FastAccess")
    (assoc db :calendar-selected key-name)
    (let [item (utils/mik-parse-int (name key-name) nil)]
      (if (and  item (> item 0))
        (assoc-in db [:calendar-selected group] item)
        db))))
(refe/reg-event-db :click-on-calendar-item click-on-calendar-item )
(refe/reg-event-db
 :clicked-cloud-item
 (fn [db [_ item]]
   (.log js/console "Clicked cloud item: " (str item))
   db))

(refe/reg-event-db
 :clicked-many-cloud-items
 (fn [db [_ items]]
   (.log js/console "Clicked many clou  items: " (str items))
   db))

(refe/reg-event-db
 :select-all-nodes
 (fn [db i]
   (.log js/console "Clicked on <select all nodes> button")
   db))

(refe/reg-event-db
 :unselect-all-nodes
 (fn [db i]
   (.log js/console "Clicked on <unselect all nodes> button")
   db))


(refe/reg-event-db
 :sort
 (fn [db [_ sort-order]]
   (.log js/console "Sorting in order: " sort-order)
   db))

(refe/reg-event-db
 :select-node
 (fn [db [_ file-path]]
   (.log js/console "Selected node by filepath: " file-path)
   db))

(refe/reg-event-db
 :file-operation
 (fn [db [_ operation-name]]
   (.log js/console "Operating on selected files: " (str (name operation-name)))
   db))

(refe/reg-event-db
 :delete-tags-from-selection
 (fn [db [_ tag-list]]
   (.log js/console "Tags that must be deleted: " tag-list)
   db))

(refe/reg-event-db
 :add-tags-to-selection
 (fn [db [_ tag-list]]
   (.log js/console "Tags that must be added to selection: " tag-list)
   db))
(refe/reg-event-db
 :submit-tagging-now
 (fn [db _]
   (.log js/console "Tagging now is submitted")
   (.log js/console "Must send to server to submit :nodes-temp-tags-to-delete and :nodes-temp-tags-to-add")
   (.log js/console "Must, also, probably, clear :nodes-selected")
   db))

(refe/reg-event-db
 :cancel-tagging-now
 (fn [db _]
   (.log js/console "Cancelling tagging now")
   (.log js/console "Must clear :nodes-selected and :nodes-temp-tags-to-delete and :nodes-temp-tags-to-add")
   db))
