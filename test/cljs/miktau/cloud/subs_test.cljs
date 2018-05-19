(ns miktau.cloud.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.cloud.subs :as miktau-subs]
            [miktau.tools :as utils]
            [miktau.cloud.events :as miktau-events]
            [clojure.data :as clojure-data]
            [miktau.cloud.demo-data-test :as demo-data]))

(defn with-diff
  [a b]
  (is (= a b)
      (str (butlast (clojure-data/diff a b)))))

(deftest testing-non-paged-results []
  (let [db (dissoc demo-data/initial-db-after-load-from-server :meta)]
    (is (= (miktau-subs/fast-access-calendar db  nil) []))
    (is (= (miktau-subs/get-db-for-test-purposes db  nil) {}))
    (is (= (miktau-subs/general-tree-subscription db  nil) []))
    (is (= (miktau-subs/cloud db  nil) []))
    (is (= (miktau-subs/calendar db  nil) {}))
    (is (= (miktau-subs/fast-access-calendar db  nil) []))
    (is (= (miktau-subs/selection-cloud db  nil) []))))

(deftest testing-cloud-filtering-should-display?
  (let [filter-fn       (miktau-subs/cloud-filtering-should-display? {:filtering "he" :meta {:page :cloud}})
        empty-filter-fn (miktau-subs/cloud-filtering-should-display? {:filtering "" :meta {:page :cloud}})
        ds [{:compare-name "yadda"}
            {:compare-name "yodda"}
            {:compare-name "hoddea"}
            {:compare-name "hea"}
            {:compare-name "uihe"}
            {:compare-name nil}
            {:compare-name 0}
            {:compare-name "ui he he he"}]]
    (is (= (filter filter-fn ds)
           [{:compare-name "hea"}
            {:compare-name "uihe"}
            {:compare-name "ui he he he"}]))
    (is (= (filter
            empty-filter-fn ds) ds))))

(deftest testing-cloud []
  ;; testing grouping ability
  (is (= (for [i (miktau-subs/cloud  demo-data/initial-db-after-load-from-server nil)]
           (assoc i :group (count (:group i))))
         (list {:group-name "work", :max-size 20, :group 19}
               {:group-name "работа_сделана", :max-size 1, :group 1})))
  ;; testing is filtering work as expected
  (is (= (mapv :compare-name (:group (first (miktau-subs/cloud (assoc demo-data/initial-db-after-load-from-server :filtering "wo") nil))))
         ["work"]))
  (is (= (mapv :compare-name (:group (first (miktau-subs/cloud (assoc demo-data/initial-db-after-load-from-server :filtering "w") nil))))
         ["moscow_market" "wiki" "work"]))
  (is (= (mapv :compare-name (:group (first (miktau-subs/cloud (assoc demo-data/initial-db-after-load-from-server :filtering "") nil))))
         ["amazon" "bibliostore" "blog" "devops" "everybook" "moscow_market" "natan" "personal" "sforim" "translator" "ui" "usecases" "wiki" "work" "zeldin" "биржа" "магазины" "скачка_источников" "согласовать"]))
  ;; testing how group can work
  (is (= (first (:group (first (miktau-subs/cloud  demo-data/initial-db-after-load-from-server nil))))
         {:name "amazon", :compare-name "amazon", :key-name :amazon, :size 2, :group :work, :weighted-size 0.1, :selected? false, :can-select? true :disabled? false}))
  (is
   (= (mapv :weighted-size (:group (first  (miktau-subs/cloud demo-data/initial-db-after-load-from-server nil))))
      [0.1 0.4 0.05 0.05 0.05 0.45 0.65 0.2 0.1 0.1 0.05 0.1 0.05 1 0.1 0.1 0.1 0.05 0.05]))
  
  ;; [TODO] test on  <filtering>
  ;; [TODO] test on  <selection>
  )


(deftest testing-general-tree []
  (let [tree-display #(str (apply str (repeat (:pad-level %) "-" )) (:name %))
        db demo-data/initial-db-after-load-from-server]
    ;; testing grouping ability
    (is (= (mapv  tree-display (miktau-subs/general-tree (:tree-tag db) 0 #{} #{}))
           ["root" "-work" "-работа_сделана"]))

    (is (= (mapv  tree-display (miktau-subs/general-tree (:tree-tag db) 0 (into #{} (keys (:cloud-can-select db))) #{}))
           ["root" "-work" "-работа_сделана"]))
    
    ;; (is (= (take 8 (mapv  tree-display (miktau-subs/general-tree (:tree-tag db) 0 (into #{} (keys (:cloud-can-select db))) #{:work})))
    ;;        ["root"
    ;;         "-work"
    ;;         "--everybook"
    ;;         "--natan"
    ;;         "---bibliostore"
    ;;         "----translator"
    ;;         "---moscow_market"
    ;;         "----amazon"]))
    
    (is (= (miktau-subs/general-tree nil nil nil nil) []))
    (is (= (miktau-subs/general-tree [] 0 [] []) []))))

(deftest testing-selection-cloud []
  ;; [TODO] test on selection
  (let [db (assoc demo-data/initial-db-after-load-from-server
                  :cloud-selected #{})]
    (is (= (miktau-subs/selection-cloud db nil) []))
    (with-diff
      (first (miktau-subs/selection-cloud (assoc  db :cloud-selected #{:amazon}) nil))
      {:name "amazon"
       :compare-name "amazon"
       :key-name :amazon
       :size 1
       :weighted-size 1
       :selected? true
       :can-select? true})
    (is (= (miktau-subs/selection-cloud (assoc  db :cloud-selected #{}) nil) []))
    (is (= (mapv :compare-name   (miktau-subs/selection-cloud (assoc db :filtering "w" :cloud-selected "amazon") nil))
         ["moscow_market" "wiki" "work"]))))

(deftest testing-is-this-datepoint-selected? []
  (let [exec-fn #(utils/is-this-datepoint-selected?
                  demo-data/initial-db-after-load-from-server %)]
    
    (is (= (exec-fn {:year 2018                   }) true))
    (is (= (exec-fn {:year 2018 :day 23           }) true))
    (is (= (exec-fn {:year 2018 :day 23 :month 11 }) true))
    
    (is (= (exec-fn {:year 2019                   }) false))
    (is (= (exec-fn {:year 2019 :day 23           }) false))
    (is (= (exec-fn {:year 2019 :day 23 :month 11 }) false))))

(deftest testing-is-it-today?
  []
  ;; TESTING date adherence
  (let [valid-point
        (assoc
         demo-data/initial-db-after-load-from-server
         :date-now
         {:year 2018  :month 4
          :day 1})
        invalid-point
        (assoc
         demo-data/initial-db-after-load-from-server
         :date-now
         {:year 2019  :month 13
          :day 43})]
    (is (= (utils/is-it-today?   valid-point [:year]) true))
    (is (= (utils/is-it-today?   valid-point [:year :month]) true))
    (is (= (utils/is-it-today?   valid-point [:day :year :month]) true))
    (is (= (utils/is-it-today? invalid-point [:year]) false))
    (is (= (utils/is-it-today? invalid-point [:year :month]) false))
    (is (= (utils/is-it-today? invalid-point [:day :year :month]) false))))


(deftest testing-fast-access-calendar []
  (is
   (=
    (miktau-subs/fast-access-calendar demo-data/initial-db-after-load-from-server nil)
    [{:name "Today",      :group "FastAccess", :can-select? true, :key-name {:year 2018, :month 4, :day 17}, :selected? false}
     {:name "This month", :group "FastAccess", :can-select? true, :key-name {:year 2018, :month 4}, :selected? false}
     {:name "This year",  :group "FastAccess", :can-select? true, :key-name {:year 2018}, :selected? false}]))
  (is
   (=
    (miktau-subs/fast-access-calendar {} nil) [])))

(deftest testing-calendar []
  ;; [TODO] test calendar on <selection>
  (is (= (:year (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))
         {:group-name "year", :max-size 14,
          :group (list
                  {:name "2018", :key-name :2018, :size 2, :group :year,  :weighted-size 0.14285714285714285, :selected? true,  :disabled? false :can-select? true}
                  {:name "2017", :key-name :2017, :size 6, :group :year,  :weighted-size 0.42857142857142855, :selected? false, :disabled? false :can-select? true}
                  {:name "2016", :key-name :2016, :size 14, :group :year, :weighted-size 1,                   :selected? false, :disabled? false :can-select? true})}))

  (is (=  (miktau-subs/calendar nil nil) {}))
  
  (with-diff
    (miktau-subs/calendar
     {:calendar {:year {:2016 14, :2017 6, :2018 2}}
      :loading? false
      :meta {:page :cloud}
      :calendar-selected {}} nil)
    {:year {:group-name "year", :max-size 14, :group
            (list {:name "2018", :key-name :2018, :size 2, :group :year, :weighted-size 0.14285714285714285, :selected? false, :disabled? true :can-select? false}
                  {:name "2017", :key-name :2017, :size 6, :group :year, :weighted-size 0.42857142857142855, :selected? false, :disabled? true :can-select? false}
                  {:name "2016", :key-name :2016, :size 14, :group :year, :weighted-size 1, :selected? false, :can-select? false :disabled? true})}})
  ;; (is (= (miktau-subs/calendar miktau.events/demo-db nil) ""))
  
  (with-diff
    (:month (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))
    {:group-name "month", :max-size 8,
     :group (list {:name "01", :key-name :1, :size 1, :group :month, :weighted-size 0.125, :selected? false, :disabled? false :can-select? true}
                  {:name "02", :key-name :2, :size 8, :group :month, :weighted-size 1, :selected? false,     :disabled? false :can-select? true}
                  {:name "03", :key-name :3, :size 1, :group :month, :weighted-size 0.125, :selected? false, :disabled? false :can-select? true}
                  {:name "04", :key-name :4, :size 4, :group :month, :weighted-size 0.5, :selected? false,   :disabled? false :can-select? true}
                  {:name "05", :key-name :5, :size 4, :group :month, :weighted-size 0.5, :selected? false,   :disabled? false :can-select? true}
                  {:name "07", :key-name :7, :size 4, :group :month, :weighted-size 0.5, :selected? false,   :disabled? false :can-select? true})})
  
  
  (is (= (mapv :name  (:group  (:day (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))))
         ["01" "02" "03" "04" "05" "07" "08" "09" "10" "11" "12" "13" "14" "15" "16" "17" "18" "19" "20" "21" "24"]))
  (is (= (mapv :key-name  (:group  (:day (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))))
         [:1 :2 :3 :4 :5 :7 :8 :9 :10 :11 :12 :13 :14 :15 :16 :17 :18 :19 :20 :21 :24]))
  (is (= (mapv :group  (:group  (:day (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))))
         (into [] (take  21 (repeat :day)))))
  (is (= (mapv :selected?  (:group  (:day (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))))
         (into [] (take  21 (repeat false))))))
