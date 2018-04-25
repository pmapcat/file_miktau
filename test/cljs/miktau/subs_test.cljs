(ns miktau.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test :as t]
            [miktau.subs :as miktau-subs]
            [miktau.utils :as utils]
            [miktau.events :as miktau-events]
            [clojure.data :as clojure-data]
            [miktau.demo-data-test :as demo-data]))


(deftest testing-can-use? []
  (is (= (miktau-subs/can-use?  demo-data/initial-db-after-load-from-server) true))
  (is (= (miktau-subs/can-use?  (assoc demo-data/initial-db-after-load-from-server :loading? true)) false))
  (is (= (miktau-subs/can-use?  nil) false)))

(deftest testing-selection-mode? []
  (is (= (miktau-subs/selection-mode?  demo-data/initial-db-after-load-from-server nil) true))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"asdads"}) nil) true))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{})         nil) false))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})      nil) true))
  (is (= (miktau-subs/selection-mode?  nil nil) false)))

(deftest testing-cloud-filtering-should-display?
  (let [filter-fn       (miktau-subs/cloud-filtering-should-display? {:filtering "he"})
        empty-filter-fn (miktau-subs/cloud-filtering-should-display? {:filtering ""})
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
         {:name "amazon", :compare-name "amazon", :key-name :amazon, :size 2, :group :work, :weighted-size 0.1, :selected? false, :can-select? true}))
  (is
   (= (mapv :weighted-size (:group (first  (miktau-subs/cloud demo-data/initial-db-after-load-from-server nil))))
      [0.1 0.4 0.05 0.05 0.05 0.45 0.65 0.2 0.1 0.1 0.05 0.1 0.05 1 0.1 0.1 0.1 0.05 0.05]))
  
  ;; [TODO] test on  <filtering>
  ;; [TODO] test on  <selection>
  )

(deftest null-testing []
  (is  (miktau-subs/filtering nil nil))
  (is  (miktau-subs/cloud nil nil))
  (is  (miktau-subs/calendar nil nil))
  (is  (miktau-subs/selection-cloud nil nil))
  (is  (miktau-subs/fast-access-calendar nil nil))
  (is  (miktau-subs/node-items nil nil))
  (is  (miktau-subs/nodes-changing nil nil)))


(deftest testing-selection-cloud []
  ;; [TODO] test on selection
  (is (= (miktau-subs/selection-cloud
          demo-data/initial-db-after-load-from-server nil)
         [])))

(deftest testing-is-this-datepoint-selected? []
  (let [exec-fn #(utils/is-this-datepoint-selected?
                  demo-data/initial-db-after-load-from-server %)]
    
    (is (= (exec-fn {:year 2018                   }) true))
    (is (= (exec-fn {:year 2018 :day 23           }) true))
    (is (= (exec-fn {:year 2018 :day 23 :month 11 }) true))
    
    (is (= (exec-fn {:year 2019                   }) false))
    (is (= (exec-fn {:year 2019 :day 23           }) false))
    (is (= (exec-fn {:year 2019 :day 23 :month 11 }) false))))

(deftest testing-node-items []
  ;; [TODO] test on selection
  (let [with-node-items-count (update  (miktau-subs/node-items demo-data/initial-db-after-load-from-server nil) :nodes count)
        only-node-items  (:nodes (miktau-subs/node-items demo-data/initial-db-after-load-from-server nil))
        db (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"/home/mik/figuratively/dar.mp4"
                                                                                 "/home/mik/figuratively/gir.mp4"
                                                                                 "/home/mik/figuratively/grar.mp4"})]
    (is (= with-node-items-count
           {:ordered-by {:inverse? false, :field :name}, :total-nodes 22, :nodes 22 :ommitted-nodes 0, :all-selected? true} ))
    (is (=  (mapv :selected? only-node-items)
            [true true true true true true true true true true true true true true true true true true true true true true]))
    (is
     (= 
      (mapv :name (filter :selected? (:nodes (miktau-subs/node-items db nil))))
      ["dar.mp4" "gir.mp4" "grar.mp4"]))
    
    (is
     (= (:tags (first (filter :selected? (:nodes (miktau-subs/node-items (assoc  db :nodes-temp-tags-to-add "aaa zzz"
                                                                                 :nodes-temp-tags-to-delete #{:work :personal}) nil)))))
         (list
          {:name "aaa", :key-name :aaa, :to-add? true, :to-delete? false, :selected? false, :can-select? false}
          {:name "blog", :key-name :blog, :to-add? false, :to-delete? false, :selected? false, :can-select? true}
          {:name "personal", :key-name :personal, :to-add? false, :to-delete? true, :selected? false, :can-select? true}
          {:name "work", :key-name :work, :to-add? false, :to-delete? true, :selected? false, :can-select? true}
          {:name "zzz", :key-name :zzz, :to-add? true, :to-delete? false, :selected? false, :can-select? false})))
    
    (is (= (take 2 only-node-items)
           (list {:id 0 :selected? true, :modified {:year 2016, :month 7, :day 21},
                  :name "blab.mp4", :all-tags (list), :file-path "/home/mik/this_must_be_it/", :tags (list)}
                 {:id 1 :selected? true, :modified {:year 2017, :month 7, :day 20},
                  :name "hello.mp4", :all-tags (list :natan :work :bibliostore :moscow_market), :file-path "/home/mik/this_must_be_it/",
                  :tags (list
                         {:name "bibliostore",   :key-name :bibliostore, :to-add? false, :to-delete? false, :selected? false, :can-select? true}
                         {:name "moscow_market", :key-name :moscow_market, :to-add? false, :to-delete? false, :selected? false, :can-select? true}
                         {:name "natan",         :key-name :natan, :to-add? false, :to-delete? false, :selected? false, :can-select? true}
                         {:name "work",          :key-name :work,  :to-add? false, :to-delete? false, :selected? false, :can-select? true})})))))

(deftest test-generate-tags-on-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"/home/mik/figuratively/dar.mp4"
                                                                                  "/home/mik/figuratively/gir.mp4"
                                                                                  "/home/mik/figuratively/grar.mp4"})]
    (is (=  (miktau-subs/generate-tags-on-selection db)
            #{:work :personal :blog :usecases}))
    (is (=  (miktau-subs/generate-tags-on-selection (assoc db :nodes-selected #{}))
            #{}))

    (is (=  (miktau-subs/generate-tags-on-selection (assoc db :nodes-selected #{"*"}))
            (into #{} (map keyword ["amazon" "bibliostore" "blog" "devops" "everybook" "moscow_market" "natan" "personal" "sforim" "translator" "UI" "usecases" "wiki" "work" "zeldin" "биржа" "магазины" "скачка_источников" "согласовать" "работа_сделана"]))))
    (is (=  (miktau-subs/generate-tags-on-selection nil)
            #{}))))

  
(deftest testing-nodes-changing
  []
  (is (= (dissoc (miktau-subs/nodes-changing demo-data/initial-db-after-load-from-server nil) :tags-to-delete)
         {:display? true, :all-selected? true, :total-amount 22, :tags-to-add ""}))
  
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing demo-data/initial-db-after-load-from-server nil)))
         {:name "amazon"
          :compare-name "amazon",
          :key-name :amazon,
          :selected? false,
          :can-select? true}))
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-temp-tags-to-delete #{:amazon}) nil)))
         {:name "amazon"
          :compare-name "amazon",
          :key-name :amazon,
          :selected? true
          :can-select? true}))
  (is (= (first (:tags-to-delete (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-temp-tags-to-delete #{:работа_сделана}
                                                                    :nodes-selected #{"/home/mik/figuratively/blab.mp4"}) nil)))
         {:name "работа_сделана"
          :compare-name "работа_сделана",
          :key-name :работа_сделана,
          :selected? true
          :can-select? true}))
  
  ;; test "not showing" of the data
  (is
   (= (dissoc (miktau-subs/nodes-changing (assoc demo-data/initial-db-after-load-from-server :nodes-selected #{}) nil) :tags-to-delete)
      {:display? false, :all-selected? false, :total-amount 0, :tags-to-add ""})))

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
                  {:name "2018", :key-name :2018, :size 2, :group :year,  :weighted-size 0.14285714285714285, :selected? false, :can-select? true}
                  {:name "2017", :key-name :2017, :size 6, :group :year,  :weighted-size 0.42857142857142855, :selected? false, :can-select? true}
                  {:name "2016", :key-name :2016, :size 14, :group :year, :weighted-size 1,                   :selected? false, :can-select? true})}))

  (is (=  (miktau-subs/calendar nil nil) {}))
  
  (is (=  (miktau-subs/calendar
           {:calendar {:year {:2016 14, :2017 6, :2018 2}}
            :loading? false
            :calendar-selected {}} nil)
          {:year {:group-name "year", :max-size 14, :group
                  (list {:name "2018", :key-name :2018, :size 2, :group :year, :weighted-size 0.14285714285714285, :selected? false, :can-select? false}
                        {:name "2017", :key-name :2017, :size 6, :group :year, :weighted-size 0.42857142857142855, :selected? false, :can-select? false}
                        {:name "2016", :key-name :2016, :size 14, :group :year, :weighted-size 1, :selected? false, :can-select? false})}}))
  ;; (is (= (miktau-subs/calendar miktau.events/demo-db nil) ""))
  
  (is (= (:month (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))
         {:group-name "month", :max-size 8,
          :group (list {:name "1", :key-name :1, :size 1, :group :month, :weighted-size 0.125, :selected? false, :can-select? true}
                       {:name "2", :key-name :2, :size 8, :group :month, :weighted-size 1, :selected? false, :can-select? true}
                       {:name "3", :key-name :3, :size 1, :group :month, :weighted-size 0.125, :selected? false, :can-select? true}
                       {:name "4", :key-name :4, :size 4, :group :month, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "5", :key-name :5, :size 4, :group :month, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "7", :key-name :7, :size 4, :group :month, :weighted-size 0.5, :selected? false, :can-select? true})}))
  (is (= (:day (miktau-subs/calendar demo-data/initial-db-after-load-from-server nil))
         {:group-name "day", :max-size 2,
          :group (list {:name "1", :key-name :1, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "2", :key-name :2, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "3", :key-name :3, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "4", :key-name :4, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "5", :key-name :5, :size 2, :group :day, :weighted-size 1, :selected? false, :can-select? true}
                       {:name "7", :key-name :7, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "8", :key-name :8, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "9", :key-name :9, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "10", :key-name :10, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "11", :key-name :11, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "12", :key-name :12, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "13", :key-name :13, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "14", :key-name :14, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "15", :key-name :15, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "16", :key-name :16, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "17", :key-name :17, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "18", :key-name :18, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "19", :key-name :19, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "20", :key-name :20, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "21", :key-name :21, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true}
                       {:name "24", :key-name :24, :size 1, :group :day, :weighted-size 0.5, :selected? false, :can-select? true})})))







