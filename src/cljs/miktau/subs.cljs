(ns miktau.subs
  (:require [re-frame.core :as refe]
            [miktau.utils :as utils]))
(refe/reg-sub
 :filtering
 (fn [db _]
   (or (db :filtering) "")))

(refe/reg-sub
 :cloud
 (fn [db _]
   (for [[group-name group] (:cloud db)]
     (let [max-size (apply max (vals group))]
       {:group-name (str (name group-name))
        :max-size   max-size
        :group
        (for [[tag tag-size] group]
          {:name    (str (name tag))
           :key-name tag
           :size     tag-size
           :group    group-name
           :wieghted-size (/ tag-size max-size)
           :selected?      (contains? (db :selected) tag)
           :can-select?    (contains? (:cloud-can-select db) tag)})}))))

(refe/reg-sub
 :calendar
 (fn [db _]
   (into
    {}
    (for [[group-name group] (:calendar db)]
      [group-name
       (let [max-size (apply max (vals group))]
         {:group-name (str (name group-name))
          :max-size max-size
          :group
          (for [[tag tag-size] group]
            {:name    (if (=  group :month)
                        (utils/month-name tag)
                        (str (name tag))) 
             :key-name tag
             :size     tag-size
             :group   group-name
             :weighted-size (/ tag-size max-size)
             :selected?      (= ((:calendar-selected db) group-name) tag)
             :can-select?    (contains? ((db :calendar-can-select) group-name) tag)})})]))))

(refe/reg-sub
 :selection-cloud
 (fn [db _]
   (for [[tag _] (db :cloud-can-select)]
          {:name    (str (name tag))
           :key-name tag
           :weighted-size 1
           :selected?      (contains? (db :selected) tag)
           :can-select?    true})))
(refe/reg-sub
 :fast-access-calendar
 (fn [db _]
   [{:name "Today"
     :group "FastAccess"
     :can-select? (utils/whether-this-date-adheres-to-now-date? db [:day :month :year])
     :key-name      (:date-now db)
     :selected? false}
    {:name "This month"
     :group "FastAccess"
     :can-select? (utils/whether-this-date-adheres-to-now-date? db [:month :year])
     :key-name      (dissoc (:date-now db) :day)
     :selected? false}
    {:name "This year"
     :group "FastAccess"
     :can-select? (utils/whether-this-date-adheres-to-now-date? db [:year])
     :key-name      {:year (:year (:date-now db))}
     :selected? false}]))

(refe/reg-sub
 :node-items
 (fn [db _]
   (let [all-selected? (=  (first (db :nodes-selected)) "*")]
     {:ordered-by
      (utils/parse-sorting-field (:nodes-sorted db))
      :total-nodes (:total-nodes db)
      :ommitted-nodes (-  (:total-nodes db) (count (db :nodes)))
      :all-selected? all-selected?
      :nodes
      (for [i (db :nodes)]
        {:selected?
         (if all-selected?
           true
           (contains? (db :nodes-sorted)
                      (:file-path i)))
         :modified
         (i :modified)
         :name (:name i)
         :all-tags (map keyword (i :tags))
         :file-path (i :file-path)
         :tags
         (for [tag (i :tags)]
           {:name    (str (name tag))
            :key-name (keyword (str tag))
            :selected?      (contains? (db :selected) (keyword tag))
            :can-select?    true})})})))

(refe/reg-sub
 :nodes-changing
 (fn [db _]
   (let [all-selected? (=  (first (db :nodes-selected)) "*")]
     {:display? (not (empty? (db :nodes-selected)))
      :all-selected? all-selected?
      :total-amount
      (if all-selected? (db :total-nodes)
          (count (db :nodes-selected)))
      :tags-to-delete
      (if all-selected?
        (into [] (map str (map name (keys (db :cloud-can-select)))))
        (map
         str
         (into
          []
          (flatten
           (for [item (db :nodes)]
             (if (contains? (db :nodes-selected) (item :file-path))
               (:tags item)
               nil))))))})))
;; (reg-sub
;;  :total
;;  (fn [db _]
;;    (db :total)))

;; (reg-sub
;;  :total.pretty
;;  (fn [db _]
;;    (try
;;      (if (db :Total)
;;        (minutes->duration-pprint (db :total))
;;        "нет данных")
;;      (catch js/Error e "нет-данных"))))
;; (reg-sub
;;  :current.pretty
;;  (fn [db _]
;;    (let [minutes (get-in db [:cur-session :MinutesWorked])]
;;      (if minutes
;;        (minutes->duration-pprint minutes)
;;        "нет данных"))))


;; (reg-sub
;;  :auth?
;;  (fn [db _]
;;    (:auth? db)))


;; (reg-sub
;;  :user
;;  (fn [db _]
;;    (or  (:user db) {})))

;; (reg-sub
;;  :cur-session
;;  (fn [db _]
;;    (or (:cur-session db) {})))

;; (reg-sub
;;  :current-session
;;  (fn [db _]
;;    (:cur-session db)))

;; (reg-sub
;;  :users.sorted
;;  (fn [db _]
;;    (:group db)
;;    (sort-by  #(str (% :Status)
;;                    (% :User)) (:group db))))
;; (reg-sub
;;  :sessions.clustered
;;  (fn [db _]
;;    (reverse
;;     (sort-by
;;      first
;;      (group-by
;;       #(:date (time->nice-repr (:Started %)))
;;       (:sessions db))))))

;; (reg-sub
;;  :error
;;  (fn [db _]
;;    (db :has-error)))

;; (reg-sub
;;  :modal
;;  (fn [db _]
;;    (:modal db)))

;; (reg-sub
;;  :ins-and-outs
;;  (fn [db _]
;;    (or  (:ins-and-outs db) [])))

;; (reg-sub
;;  :loading?
;;  (fn [db _]
;;    (= :loading (db :status))))


