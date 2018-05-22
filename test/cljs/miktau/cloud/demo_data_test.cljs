(ns miktau.cloud.demo-data-test
  (:require [miktau.meta-db :as meta-db]))

(def demo-db
  {:tree-tag {}
   :filtering ""
   :meta (meta-db/set-page meta-db/meta-db :cloud)
   :total-nodes 0
   
   :cloud-selected #{:blab}
   :cloud  {:VolutPatem {:blab 43 :blip 27 :blop 12}}
   :cloud-can-select {:blip true :blop true}

   :breadcrumbs-show-all? false   

   
   :date-now {:year 2016 :month 7 :day 21}   
   :calendar-selected {:year  2018  :day 23 :month 11}
   :calendar   {:year {2018 12 2017 13 2016 12}
                :month {12 1 13 1 14 2}
                :day   {1 3 2 3 3 4}}
   :calendar-can-select {:year {2018 2}
                         :month {11 3}
                         :day   {9 3}}})



(def initial-db-after-load-from-server
  {:cloud-can-select {:moscow_market true, :devops true, :personal true, :usecases true, :биржа true, :amazon true, :магазины true, :wiki true, :work true, :sforim true, :согласовать true, :natan true, :работа_сделана true, :bibliostore true, :translator true, :скачка_источников true, :everybook true, :UI true, :blog true, :zeldin true},
   :cloud {:root {:moscow_market 9, :devops 1, :personal 4, :usecases 2, :биржа 2, :amazon 2, :магазины 2, :wiki 1, :work 20, :sforim 2, :согласовать 1, :natan 13, :bibliostore 8, :translator 2, :скачка_источников 1, :everybook 1, :UI 1, :blog 1, :zeldin 2 :работа_сделана 1}},
   :date-now {:year 2018, :month 4, :day 17},   
   :tree-tag {:name "root", :children {:work {:name "work", :children {:everybook {:name "everybook", :children {}}, :natan {:name "natan", :children {:bibliostore {:name "bibliostore", :children {:translator {:name "translator", :children {}}}}, :moscow_market {:name "moscow_market", :children {:amazon {:name "amazon", :children {}}, :bibliostore {:name "bibliostore", :children {:translator {:name "translator", :children {:amazon {:name "amazon", :children {:devops {:name "devops", :children {}}}}}}, :магазины {:name "магазины", :children {}}}}, :биржа {:name "биржа", :children {:скачка_источников {:name "скачка_источников", :children {}}}}}}, :sforim {:name "sforim", :children {:wiki {:name "wiki", :children {:согласовать {:name "согласовать", :children {}}}}}}, :биржа {:name "биржа", :children {:UI {:name "UI", :children {}}}}, :магазины {:name "магазины", :children {:sforim {:name "sforim", :children {}}}}}}, :personal {:name "personal", :children {:blog {:name "blog", :children {}}, :usecases {:name "usecases", :children {}}}}, :zeldin {:name "zeldin", :children {}}}}, :работа_сделана {:name "работа_сделана", :children {}}}}
   :filtering "",
   :meta (meta-db/set-page meta-db/meta-db :cloud)
   :cloud-selected #{:blab},
   :calendar {:year {2016 14, 2017 6, 2018 2},
              :month {1 1, 2 8, 3 1, 4 4, 5 4, 7 4},
              :day {14 1, 18 1, 12 1, 11 1, 24 1, 10 1, 21 1, 13 1, 4 1, 16 1, 7 1, 1 1, 8 1, 9 1, 20 1, 17 1, 19 1, 2 1, 5 2, 15 1, 3 1}},
   :total-nodes 22

   :breadcrumbs-show-all? false      
   :calendar-selected   {:year 2018, :day 23, :month 11},
   :calendar-can-select {:year  {2016 14, 2017 6, 2018 2},
                         :month {1 1, 2 8, 3 1, 4 4, 5 4, 7 4},
                         :day {14 1, 18 1, 12 1, 11 1, 24 1, 10 1, 21 1, 13 1, 4 1, 16 1, 7 1, 1 1, 8 1, 9 1, 20 1, 17 1, 19 1, 2 1, 5 2, 15 1, 3 1}}})
