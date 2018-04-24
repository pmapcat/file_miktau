(ns miktau.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.events :as miktau-events]
            [miktau.demo-data-test :as demo-data]))

(deftest test-getting-default-app-data []
  (is
   (= (miktau-events/got-app-data
       {:db miktau.events/demo-db}
       [nil  demo-data/demo-response])
      demo-data/initial-db-after-load-from-server)))

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
          (-> (miktau-events/filtering db [nil "hello"])
              (miktau-events/clear        [nil "blab"]))) ""))))

(deftest test-click-on-calendar-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :year :2010]))
      {:year 2010}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :drozd nil]))
      {}))
    
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil :drozd -23]))
      {}))
    
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010}) [nil :year :2010]))
      {:year nil}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 19}) [nil :day :19]))
      {:year 2010 :day nil}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20}) [nil :day :19]))
      {:year 2010 :day 19}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20}) [nil :month :3]))
      {:year 2010 :day 20 :month 3}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20 :month 3}) [nil :month :3]))
      {:year 2010 :day 20 :month nil}))))

(deftest test-click-on-fast-access-item []
  (let [db (assoc demo-data/initial-db-after-load-from-server :calendar-selected {})]
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" {:year 2018 :month 3}]))
      {:year 2018 :month 3}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" nil]))
      {}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil nil nil]))
      {}))
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item db [nil "FastAccess" {}]))
      {}))
    
    (is
     (= 
      (:calendar-selected
       (miktau-events/click-on-calendar-item (assoc  db :calendar-selected {:year 2018 :month 3}) [nil "FastAccess" {:year 2018 :month 3}]))
      {}))
    (is
     (= 
      (:calendar-selected (miktau-events/click-on-calendar-item (assoc db :calendar-selected {:year 2010 :day 20 :month 3}) [nil "FastAccess" {:year 2018}]))
      {:year 2018}))))

(deftest test-click-on-cloud []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    ;; no selection is available when click happens
    ;; clear caching should happen also
    
    
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil :work])) #{:work}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud (assoc db :cloud-selected #{:zanoza}) [nil :work])) #{:zanoza :work}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud (assoc db :cloud-selected #{:work}) [nil :work])) #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil nil]))     #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil 123]))     #{}))
    (is (=  (:cloud-selected  (miktau-events/click-on-cloud db [nil "graws"])) #{}))))


(deftest test-clicked-many-cloud-items []
  (let [db (assoc demo-data/initial-db-after-load-from-server :cloud-selected #{})]
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items db [nil [:hello :world]])) #{:hello :world}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items db [nil [nil nil]])) #{}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil [:za :zo]])) #{:za :zo}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil (into '() [:za :zo])])) #{:za :zo}))
    (is (=  (:cloud-selected  (miktau-events/clicked-many-cloud-items (assoc  db :cloud-selected #{:ha :ho}) [nil nil])) #{:ha :ho}))))

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
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil "-name"])) "-name"))
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil nil])) "name"))
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil :kliqo])) "name"))
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil "modified"])) "modified"))
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil "-modified"])) "-modified"))
    (is (=  (:nodes-sorted  (miktau-events/sort-nodes db [nil "name"])) "name"))))

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
    (is (=  (:api-call (miktau-events/file-operation-fx {:db db} [nil :default]))
            {:url "/api/bulk-operate-on-files",
             :params {:action "default", :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}}))))

(deftest test-delete-tag-from-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:blab}) [nil :hello])) #{:hello :blab}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil :hello])) #{:hello}))
    (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil nil])) #{}))))

(deftest test-add-tag-to-selection []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
    ;; if nothing selected
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection (assoc db :nodes-temp-tags-to-add #{:blab}) [nil :hello])) #{:hello :blab}))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil :hello])) #{:hello}))
    (is (=  (:nodes-temp-tags-to-add (miktau-events/add-tag-to-selection db [nil nil])) #{}))))

(deftest test-submit-tagging-fx []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"}
                    :nodes-temp-tags-to-add #{:blop}
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add    (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{}))
    (is (= (:nodes-temp-tags-to-delete (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{}))
    (is (= (:nodes-selected            (:db (miktau-events/submit-tagging-fx {:db db} nil))) #{}))
    
    (is (= (:api-call (miktau-events/submit-tagging-fx {:db db} nil))
           {:url "/api/update-records"
            :params {:tags-to-add       ["blop"]
                     :tags-to-delete    ["hom"]
                     :request
                      {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}}))))

(deftest test-cancel-tagging []
  (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"}
                    :nodes-temp-tags-to-add #{:blop}
                    :nodes-temp-tags-to-delete #{:hom})]
    (is (= (:nodes-temp-tags-to-add     (miktau-events/cancel-tagging  db nil)) #{}))
    (is (= (:nodes-temp-tags-to-delete  (miktau-events/cancel-tagging  db nil)) #{}))
    (is (= (:nodes-selected             (miktau-events/cancel-tagging  db nil)) #{}))
    (is (= (:nodes-selected             (miktau-events/cancel-tagging  nil nil)) #{}))))

;; (deftest test-add-tag-from-selection []
;;   (let [db  (assoc  demo-data/initial-db-after-load-from-server :nodes-selected #{"*"})]
;;     ;; if nothing selected
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection (assoc db :nodes-temp-tags-to-delete #{:blab}) [nil :hello])) #{:hello :blab}))
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil :hello])) #{:hello}))
;;     (is (=  (:nodes-temp-tags-to-delete (miktau-events/delete-tag-from-selection db [nil nil])) #{}))))
