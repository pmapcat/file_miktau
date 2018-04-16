(ns miktau.events
  (:require
   [day8.re-frame.http-fx]
   [miktau.utils :as utils]
   [clojure.string :as clojure-string]
   [re-frame.core :as refe]))

(refe/reg-event-db
 :init-db
 (fn [_ _]
   {:loading? true
    :filtering ""
    
    :nodes-sorted "-name"
    :core-directory ":test:"
    :date-now {:year 2016 :month 7 :day 21}
    
    :nodes [{:id 0, :name "blab.mp4" :file-path "/home/mik/this_must_be_it/" :tags []
             :modified {:year 2016 :month 7 :day 21}}]
    :nodes-selected #{"*"}
    
    :cloud-selected #{:blab}
    :cloud  {:VolutPatem {:blab 43 :blip 27 :blop 12}}
    :cloud-can-select {:blip true :blop true}
    
    :calendar-selected {:year  2018  :day 23 :month 11}
    :calendar   {:year {:2018 12 :2017 13 :2016 12}
                 :month {:12 1 :13 1 :14 2}
                 :day   {:1 3 :2 3 :3 4}}
    :calendar-can-select {:year {:2018 2}
                          :month {:11 3}
                          :day   {:9 3}}}))

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

(refe/reg-event-db
 :got-app-data
 (fn [{:keys [db]} [_ response]]
   (assoc
    (merge
     db
     response)
    db :loading? false)))

(comment
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
   (.log js/console "registered <back> event")))

(refe/reg-event-db
 :filtering
 (fn [_ [_ data]]
   (.log js/console "registered filtering event on: " data)))

(refe/reg-event-db
 :clear
 (fn [db _]
   (.log js/console "registered clearing event on: " data)))

(refe/reg-event-db
 :click-on-calendar-item
 (fn [db [_ group key-name]]
   (if (=  group "FastAccess")
     (.log js/console "registered point clicking event on: " (str key-name))
     (.log js/console "registered clicking event on calendar: " (str group) " " (str key-name)))))

(refe/reg-event-db
 :clicked-cloud-item
 (fn [db [_ item]]
   (.log js/console "Clicked cloud item: " (str item))))

(refe/reg-event-db
 :clicked-many-cloud-items
 (fn [db [_ items]]
   (.log js/console "Clicked many clou  items: " (str items))))


(refe/reg-event-db
 :select-all-nodes
 (fn [db i]
   (.log js/console "Clicked on <select all nodes> button")))

(refe/reg-event-db
 :unselect-all-nodes
 (fn [db i]
   (.log js/console "Clicked on <unselect all nodes> button")))


(refe/reg-event-db
 :sort
 (fn [db [_ sort-order]]
   (.log js/console "Sorting in order: " sort-order)))

(refe/reg-event-db
 :select-node
 (fn [db [_ file-path]]
   (.log js/console "Selected node by filepath: " filepath)))

(refe/reg-event-db
 :file-operation
 (fn [db [_ operation-name]]
   (.log js/console "Operating on selected files: " (str (name operation-name)))))











;; (refe/reg-event-db
;;  :on-initialized
;;  (fn [_ [_ new-db-state]]
;;    (assoc new-db-state :status :ready
;;           :csrf-token (aget js/window "csrftoken"))))

;; (refe/reg-event-fx                           
;;   :login
;;   (fn [{:keys [db]} [_ user pass]]
;;     {:db   (assoc db :status :loading
;;                      :username user)
;;      :http-xhrio {:method          :post
;;                   :uri             "/tabel/users/check_credentials/"
;;                   :response-format (ajax/json-response-format {:keywords? true})
;;                   :format          (ajax/json-request-format)
;;                   :params          {:username user :password pass}
;;                   :headers         {"X-CSRFToken" (db :csrf-token)}
;;                   :timeout         8000
;;                   :on-success      [:on-login]
;;                   :on-failure      [:http-error]}}))

;; (refe/reg-event-fx
;;  :set-user-meta
;;  (fn [{:keys [db]} [_ key val on-after]]
;;    (let [us-meta (get-in db [:user :Meta])]
;;      {:db (assoc db :status :loading)
;;       :http-xhrio
;;       {:method          :post
;;        :uri             (str "/tabel/users/" (:userid db)  "/store-user-meta/")
;;        :response-format (ajax/json-response-format {:keywords? true})
;;        :format          (ajax/json-request-format)
;;        :params          {:meta (data->str (assoc us-meta key val))}
;;        :headers         {"X-CSRFToken" (db :csrf-token)}
;;        :timeout         8000
;;        :on-success      [on-after]
;;        :on-failure      [:http-error]}})))

;; (refe/reg-event-fx
;;  :do-break
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect  [:set-user-meta :break-type? "BREAK" :not.view.do-out]}))

;; (refe/reg-event-fx
;;  :do-out
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect  [:set-user-meta :break-type? "OUT" :not.view.do-out]}))

;; (reg-event-fx
;;  :where?.office
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect [:set-user-meta :where? "ОФИС" :get-app-data-from-server]}))

;; (reg-event-fx
;;  :where?.transit
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect [:set-user-meta :where? "ПОЕЗДКА" :get-app-data-from-server]}))

;; (reg-event-fx
;;  :where?.home
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect [:set-user-meta :where? "ДОМ" :get-app-data-from-server]}))

;; (reg-event-fx
;;  :where?.storage
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :fx-redirect [:set-user-meta :where? "СКЛАД" :get-app-data-from-server]}))

;; (reg-event-fx
;;  :get-app-data-from-server
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :http-xhrio {:method          :get
;;                  :uri             "/tabel/users/get_app_state/"
;;                  :response-format (ajax/json-response-format {:keywords? true})
;;                  :params          {:username (db :username)}
;;                  :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                  :timeout         8000
;;                  :on-success      [:on-got-data-from-server]
;;                  :on-failure      [:http-error]}}))


;; (reg-event-fx
;;  :on-got-data-from-server
;;  (fn [{:keys [db]} [_ bulk]]
;;    (let [now       (events-utils/now)
;;          user      (events-utils/prep-user (:user bulk))
;;          sessions  (events-utils/prep-sessions (:working_sessions bulk))
;;          group     (map events-utils/prep-user (:user_group bulk))
;;          db-finite
;;          (assoc
;;           db
;;           :user user
;;           :status :ready
;;           :userid (:Id user)
;;           :cur-session
;;           (or  (first  (filter #(= (:Id %) (user :LastSessid)) sessions)) nil)
;;           :total
;;           (reduce + (map :MinutesWorked sessions))
;;           :sessions
;;           sessions
;;           :group group)]
;;      {:notify! (clojure.string/join "\n" (events-utils/ins-and-outs (:group db) group))
;;       :store!  (assoc db-finite :modal nil)
;;       :db db-finite})))

;; (reg-event-fx
;;  :logout
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :auth? false)
;;     :store! (assoc db :auth? false :modal nil)
;;     :notify! "Logged out"}))

;; (reg-event-fx
;;  :on-login
;;  (fn [{:keys [db]} [_ body]]
;;    (let [valid-user? (:valid_user body)]
;;      (if valid-user?
;;        {:db (assoc db :status :loading :auth? true)
;;         :http-xhrio {:method          :get
;;                      :uri             "/tabel/users/get_app_state/"
;;                      :response-format (ajax/json-response-format {:keywords? true})
;;                      :params          {:username (db :username)}
;;                      :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                      :timeout         8000
;;                      :on-success      [:on-got-data-from-server]
;;                      :on-failure      [:http-error]}}
;;        {:db (assoc db :status :ready :auth? false)}))))


;; ;;       ============================= MUTABLE CHANGES =========================
;; (reg-event-fx
;;  :do-in
;;  (fn [{:keys [db]} [_ transit?]]
;;    {:db (assoc db :status :loading)
;;     :http-xhrio {:method          :post
;;                  :uri             (str "/tabel/users/" (:userid db) "/do-in/")
;;                  :format          (ajax/json-request-format)                                  
;;                  :response-format (ajax/json-response-format {:keywords? true})
;;                  :params          {:user (db  :username) :project "n/a" :is_transit transit? }
;;                  :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                  :timeout         8000
;;                  :on-success      (if transit? [:where?.transit]
;;                                       [:get-app-data-from-server])
;;                  :on-failure      [:http-error]}}))

;; (reg-event-fx
;;  :not.view.do-out
;;  (fn [{:keys [db]} _]
;;    {:db (assoc db :status :loading)
;;     :http-xhrio {:method          :post
;;                  :uri             (str "/tabel/users/" (:userid db) "/do-out/")
;;                  :response-format (ajax/json-response-format {:keywords? true})
;;                  :format          (ajax/json-request-format)                 
;;                  :params          {:username (db  :username) :password (db :pass)}
;;                  :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                  :timeout         8000
;;                  :on-success      [:get-app-data-from-server]
;;                  :on-failure      [:http-error]}}))

;; (reg-event-fx
;;  :delete-session
;;  (fn [{:keys [db]} [_ sessid]]
;;    {:db (assoc db :status :loading)
;;     :http-xhrio {:method          :delete
;;                  :uri             (str  "/tabel/workingsessions/" sessid "/")
;;                  :response-format (ajax/json-response-format {:keywords? true})
;;                  :format          (ajax/json-request-format)
;;                  :params          {:username (db  :username) :password (db :pass)}
;;                  :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                  :timeout         8000
;;                  :on-success      [:get-app-data-from-server]
;;                  :on-failure      [:http-error]}}))

;; (reg-event-fx
;;  :change-session
;;  (fn [{:keys [db]} [_ sessid started ended project is-transit]]
;;    {:db (assoc db :status :loading)
;;     :http-xhrio {:method          :put
;;                  :uri             (str "/tabel/workingsessions/" sessid "/")
;;                  :response-format (ajax/json-response-format {:keywords? true})
;;                  :format          (ajax/json-request-format)
;;                  :params          {:started (ct/to-string (local-time->utc started))
;;                                    :ended   (ct/to-string (local-time->utc ended))
;;                                    :project (or project "n/a")
;;                                    :minutes_worked 0 
;;                                    :is_transit is-transit}
;;                  :headers         {"X-CSRFToken" (db  :csrf-token)}
;;                  :timeout         8000
;;                  :on-success      [:get-app-data-from-server]
;;                  :on-failure      [:http-error]}}))

;; (reg-event-db
;;  :modal
;;  (fn [db [_ modal]]
;;    (assoc db :modal modal)))

;; (reg-event-db
;;  :dismiss-modal
;;  (fn [db _]
;;    (assoc db :modal nil)))
