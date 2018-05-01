(ns miktau.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.events :as miktau-events]
            [miktau.utils  :as utils]
            [clojure.data :as clojure-data]
            [miktau.demo-data-test :as demo-data]))
(defn with-diff
  [a b]
  (is (= a b)
      (str (butlast (clojure-data/diff a b)))))
(deftest test-clicking-on-disabled-cloud-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{:hem :in :dal})]
    (is (=  (:nodes-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) #{}))
    (is (=  (:cloud-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) #{:hello}))
    (is (=  (:cloud-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) #{:hello}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-disabled-cloud {:db  db} [nil :hello]))) {}))))

(deftest test-clicking-on-disabled-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{:hem :in :dal})]
    (is (=  (:nodes-selected (:db (miktau-events/click-on-disabled-calendar {:db  db} [nil :year :2018]))) #{}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-disabled-calendar {:db  db} [nil :year :2018]))) {:year 2018}))))


(deftest test-getting-default-app-data []
  (with-diff
    (miktau-events/got-app-data miktau.demo-data-test/demo-db [nil  demo-data/demo-response])
    demo-data/initial-db-after-load-from-server))

(deftest test-filtering []
  (let [db demo-data/initial-db-after-load-from-server]
    (is (= (:filtering (miktau-events/filtering db [nil "h"])) "h"))
    (is (= (:filtering (miktau-events/filtering db [nil ""])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil "****"])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil "bibilo@@@"])) ""))
    (is (= (:filtering (miktau-events/filtering db [nil ""])) ""))
    
    (is (= (:filtering (miktau-events/filtering db [nil "hello"])) "hello"))
    (is (= (:filtering (miktau-events/filtering db [nil nil])) ""))
    (is (=
         (:filtering
          (-> (miktau-events/filtering db [nil "hello"])
              (miktau-events/filtering    [nil "blab"]))) "blab"))
    (is (=
         (:filtering
          (:db
           (-> (miktau-events/filtering db [nil "hello"])
               (miktau-events/clear        [nil "blab"])))) ""))))

(deftest test-click-on-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :year :2010]))) {:year 2010}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :drozd nil]))) {}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil :drozd -23]))) {}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010})} [nil :year :2010]))) {:year nil}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 19})} [nil :day :19]))) {:year 2010 :day nil}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20})} [nil :day :19]))) {:year 2010 :day 19}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20})} [nil :month :3]))) {:year 2010 :day 20 :month 3}))
    (is (= (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20 :month 3})} [nil :month :3]))) {:year 2010 :day 20 :month nil}))))

(deftest test-click-on-fast-access-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" {:year 2018 :month 3}]))) {:year 2018 :month 3}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil nil nil]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db db} [nil "FastAccess" {}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc  db :calendar-selected {:year 2018 :month 3})} [nil "FastAccess" {:year 2018 :month 3}]))) {}))
    (is (=  (:calendar-selected (:db (miktau-events/click-on-calendar-item {:db (assoc db :calendar-selected {:year 2010 :day 20 :month 3})} [nil "FastAccess" {:year 2018}]))) {:year 2018}))))

(deftest test-click-on-cloud []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    ;; no selection is available when click happens
    ;; clear caching should happen also
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil :work]))) #{:work}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db (assoc db :cloud-selected #{:zanoza})} [nil :work]))) #{:zanoza :work}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db (assoc db :cloud-selected #{:work})} [nil :work]))) #{}))
    
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil nil])))     #{}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil 123])))     #{}))
    (is (=  (:cloud-selected  (:db (miktau-events/click-on-cloud {:db db} [nil "graws"]))) #{}))))


(deftest test-clicked-many-cloud-items []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db (assoc db
                                                                                       :cloud-selected #{:hello :world})} [nil [:hello :world]]))) #{}))
    
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db db} [nil [:hello :world]]))) #{:hello :world}))
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db db} [nil [nil nil]]))) #{}))
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db (assoc  db :cloud-selected #{:ha :ho})} [nil [:za :zo]]))) #{:za :zo}))
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db (assoc  db :cloud-selected #{:ha :ho})} [nil (into '() [:za :zo])]))) #{:za :zo}))
    (is (=  (:cloud-selected  (:db (miktau-events/clicked-many-cloud-items {:db (assoc  db :cloud-selected #{:ha :ho})} [nil nil]))) #{:ha :ho}))))

(deftest test-select-all-nodes []
  (let [db (assoc demo-data/initial-db-after-load-from-server :nodes-selected #{})]
    
    ;; if nothing selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes db [nil nil])) #{"*"}))
    ;; if aleready something selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"blab" "blip"}) [nil nil])) #{"*"}))
    ;; if already all selected
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"*"}) [nil nil])) #{}))
    ;; if already all selected, but some are not
    (is (=  (:nodes-selected  (miktau-events/select-all-nodes (assoc  db :nodes-selected #{"diro" "doro" "*"}) [nil nil])) #{}))))

(deftest test-sort-nodes []
  (let [db  demo-data/initial-db-after-load-from-server]
    
    ;; if nothing selected
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "-name"]))) "-name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil nil]))) "name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil :kliqo]))) "name"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "modified"]))) "modified"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "-modified"]))) "-modified"))
    (is (=  (:nodes-sorted  (:db (miktau-events/sort-nodes db [nil "name"]))) "name"))))

(deftest test-select-node []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{})]
    ;; if nothing selected
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil nil])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil "hello"])) #{"hello"}))
    (is (=  (:nodes-selected  (miktau-events/select-node db [nil nil])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"hello"}) [nil "hello"])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"hello"}) [nil "world"])) #{"hello" "world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"*"}) [nil "world"])) #{"world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"*" "zizo"}) [nil "world"])) #{"world"}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"zizo"}) [nil "zizo"])) #{}))
    (is (=  (:nodes-selected  (miktau-events/select-node (assoc  db :nodes-selected #{"zizo"}) [nil "blab"])) #{"zizo" "blab"}))))

(deftest test-file-opreration []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (miktau-events/file-operation-fx {:db db} [nil nil]) {:db db}))
    (is (=   (utils/clean-server-call-for-tests (:http-xhrio (miktau-events/file-operation-fx {:db db} [nil :default])))
             {:method :post, :uri "/api/bulk-operate-on-files",
              :timeout 8000,
              :params {:action "default",
                       :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}},
              :on-success [:mutable-server-operation],
              :on-failure [:http-error]}))))


(deftest test-delete-tag-from-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:blab}) [nil :hello])) #{:hello :blab}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil :hello])) #{:hello}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:hello}) [nil :hello])) #{}))
    
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil nil])) #{}))))

(deftest test-add-tag-to-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection (assoc db :nodes-temp-tags-to-add "zero vasya galibob") [nil "zab"])) "zab"))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil "blob"])) "blob"))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil nil])) ""))))
(deftest test-build-drill []
  (let [db {:nodes-temp-tags-to-add "blop glop" :nodes-temp-tags-to-delete #{:hom} :cloud-selected #{:hello :hom}}]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{:blop :glop :hello})))
  (let [db {:nodes-temp-tags-to-add "" :nodes-temp-tags-to-delete #{:hom :hello :dello} :cloud-selected #{:hello :hom}}]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{})))
  (let [db {:nodes-temp-tags-to-add "hello world" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{:hello :world})))
  (let [db {:nodes-temp-tags-to-add "hello" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{:hello})))
  (let [db nil]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{})))
  
  (let [db {:nodes-temp-tags-to-add "" :nodes-temp-tags-to-delete #{:hello :world} :cloud-selected #{:hello :world}}]
    (is (= (:cloud-selected (miktau-events/build-drill db))
           #{}))))

(deftest test-submit-tagging-fx []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server
                    :nodes-selected #{"*"}
                    :cloud-selected #{:hello :hom}
                    :nodes-temp-tags-to-add "blop glop"
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add    (:db (miktau-events/submit-tagging-fx {:db db} nil))) ""))
    (is (= (:nodes-temp-tags-to-delete (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{}))
    (is (= (:nodes-selected            (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{}))
    (is (= (:cloud-selected            (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{:blop :glop :hello}))
    
    (is (= (utils/clean-server-call-for-tests (:http-xhrio (miktau-events/submit-tagging-fx {:db db} nil)))
           {:method :post,
            :uri "/api/update-records",
            :timeout 8000,
            :params {:tags-to-add ["blop" "glop"], :tags-to-delete ["hom"], :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["hello" "hom"]}},
            :on-success [:mutable-server-operation],
            :on-failure [:http-error]}))))

(deftest test-cancel-tagging []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"}
                    :nodes-temp-tags-to-add #{:blop}
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add     (miktau-events/cancel-tagging  db nil)) ""))
    (is (= (:nodes-temp-tags-to-delete  (miktau-events/cancel-tagging  db nil)) #{}))
    (is (= (:nodes-selected             (miktau-events/cancel-tagging  db nil)) #{}))
    (is (= (:nodes-selected             (miktau-events/cancel-tagging  nil nil)) #{}))))

;; (deftest test-add-tag-from-selection []
;;   (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
;;     ;; if nothing selected
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:blab}) [nil :hello])) #{:hello :blab}))
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil :hello])) #{:hello}))
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil nil])) #{}))))
