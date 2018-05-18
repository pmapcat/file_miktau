(ns miktau.nodes.subs-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.nodes.subs :as miktau-subs]
            [miktau.nodes.demo-data-test :as demo-data]))

(deftest testing-selection-mode? []
  
  (is (= (miktau-subs/selection-mode?  demo-data/initial-db-after-load-from-server nil) true))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"asdads"}) nil) true))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{})         nil) false))
  (is (= (miktau-subs/selection-mode?  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})      nil) true))
  (is (= (miktau-subs/selection-mode?  nil nil) false)))


(deftest testing-node-items []
  ;; [TODO] test on selection
  (let [with-node-items-count (update  (miktau-subs/node-items demo-data/initial-db-after-load-from-server nil) :nodes count)
        only-node-items  (:nodes (miktau-subs/node-items demo-data/initial-db-after-load-from-server nil))
        db (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"/home/mik/figuratively/dar.mp4"
                                                                                 "/home/mik/figuratively/gir.mp4"
                                                                                 "/home/mik/figuratively/grar.mp4"})]
    (is (= with-node-items-count
           {:ordered-by {:inverse? false, :field :name}, :total-nodes 22, :nodes 22 :omitted-nodes 0, :all-selected? true} ))
    (is (=  (mapv :selected? only-node-items)
            [true true true true true true true true true true true true true true true true true true true true true true]))
    (is
     (= 
      (mapv :name (filter :selected? (:nodes (miktau-subs/node-items db nil))))
      ["dar.mp4" "gir.mp4" "grar.mp4"]))
    (is (=  (:omitted-nodes with-node-items-count) 0))

    
    ;; THIS IS CROSS JOINED FUNCTIONALITY THAT I DON'T HAVE RIGHT NOW
    ;; (is
    ;;  (= (:tags (first (filter :selected? (:nodes (miktau-subs/node-items (assoc  db :nodes-temp-tags-to-add "aaa zzz"
    ;;                                                                              :nodes-temp-tags-to-delete #{:work :personal}) nil)))))
    ;;      (list
    ;;       {:name "aaa", :key-name :aaa, :to-add? true, :to-delete? false, :selected? false, :can-select? false}
    ;;       {:name "blog", :key-name :blog, :to-add? false, :to-delete? false, :selected? false, :can-select? true}
    ;;       {:name "personal", :key-name :personal, :to-add? false, :to-delete? true, :selected? false, :can-select? true}
    ;;       {:name "work", :key-name :work, :to-add? false, :to-delete? true, :selected? false, :can-select? true}
    ;;       {:name "zzz", :key-name :zzz, :to-add? true, :to-delete? false, :selected? false, :can-select? false})))
    
    (let [predef-sub-db (:nodes (miktau-subs/node-items (assoc  db :nodes-temp-tags-to-add "aaa zzz"
                                                                :nodes-temp-tags-to-delete #{:work :personal}) nil))]
      
      (is (= (mapv :name (:tags (second (filter (comp not :selected?) predef-sub-db))))
             ["bibliostore" "moscow_market" "natan" "work"]))
      (is (= (mapv :to-delete? (:tags (second (filter (comp not :selected?) predef-sub-db))))
             [false false false false]))
      (is (= (mapv :to-add? (:tags (second (filter (comp not :selected?) predef-sub-db))))
             [false false false false])))
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
