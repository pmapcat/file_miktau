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
    
    :nodes [{:name "hello.mkv" :tags #{"blab" "blip" "blop"} :modified "2018.02.03"}]
    :nodes-selected #{"*"}
    :nodes-sorted {:field :name :reverse? false}

    :cloud-selected #{"blab"}
    :cloud  {"VolutPatem" {"blab" 43 "blip" 27 "blop" 12}}
    :cloud-can-select #{"blip"}
    
    :calendar-selected {:year  "2018"  :day "23" :month "11"}
    :calendar   {:year ["2018" "2017" "2016"]
                 :month ["January" "February" "Whatever"]
                 :day   ["1" "2" "3"]}
    :calendar-can-select {:year #{}
                          :month #{}
                          :day   #{}}}))

(defn http-error
  [db response]
  {:db     (assoc db :loading? false)
   :log!   (str response)})

(utils/register-server-roundtrip
 :get-app-data
 {:method :get :url "/get-app-data/"}
 (fn [db]
   {:cloud-selected (into [] (db :cloud-selected))
    :calendar-selected (db :calendar-selected)
    :nodes-sorted (db :nodes-sorted)})
 (fn [db]
   (assoc db :loading? true))
 (fn [db response]
   {:db
    (assoc
     db :loading? true
     :nodes-selected #{}
     :nodes (:nodes response))
    (response ) (assoc  db :loading? false)})
 http-error)

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
