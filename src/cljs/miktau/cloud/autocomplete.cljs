(ns miktau.cloud.autocomplete
  (:require [reagent.core :as reagent]
            [clojure.string :as cljs-string]))

(defn e->content
  [e]
  (str
   (aget e "target" "value" )))
(defn- change-cur-input
  [app-state input]
  (swap! app-state
         (fn [db ]
           (-> db
               (assoc :cur-input input)
               (assoc :cur-index 0)))))

(defn default-render-fn
  [cur-input selected? text]
  [:div.unstyled-link.complete {:class  (if  selected?  " complete selected " "")}
   [:span {:style {:color "green" }}
    cur-input]
   (apply str (drop (count cur-input) text))])


(defn autocomplete-widget
  "Params are:
    {:completes
     [\"hello\"
      \"various\"
      \"different\"
      \"world\"] 
     :can-enter-new? false
     :display-size 10
     :render-fn (fn [now-input-str selected?-bool item-name-str])
     :submit-fn (fn [data])}"
  [completions params]
  (let [app-state (reagent/atom {:focus? false :cur-index 0 :cur-input ""})
        can-enter-new? (or (:can-enter-new? params) false)
        display-size (or (:display-size params) 10)
        render-fn (or (:render-fn params) default-render-fn)
        submit-fn-raw  (or (:submit-fn params) identity)
        submit-fn
        (fn [input]
          (submit-fn-raw input)
          (swap! app-state assoc :cur-input ""))]
    (fn [completions params]
      (let [aps @app-state
            cur-input (:cur-input aps)
            cur-index (:cur-index aps)
            filtered-items
            (for [[index item] (map list (range) (filter (fn [item] (cljs-string/starts-with?   item  cur-input)) completions))]
              {:selected? (= index cur-index)
               :name   item})
            complete-placeholder (or (:name (first (filter :selected? filtered-items))) "")]
        [:div {:style {:position "relative"}}
         [:input {:value (:cur-input aps)
                  :style {:width "100%" :height "1.9em" :padding-left "10px" :background "transparent"}
                  :type "text"
                  :on-change
                  #(change-cur-input app-state (e->content %))
                  :on-key-down
                  #(condp = (aget % "key")
                     "Enter"
                     (if-not can-enter-new?
                       (submit-fn complete-placeholder)
                       (submit-fn (e->content %)))
                     "ArrowDown"
                     (swap! app-state assoc :cur-index (mod (inc cur-index) (count filtered-items)))
                     "ArrowUp"
                     (swap! app-state assoc :cur-index (mod (dec cur-index) (count filtered-items)))
                     "ArrowRight"
                     (change-cur-input app-state complete-placeholder)
                     "Tab"
                     (do
                       (cond
                         (= 1 (count filtered-items))
                         (change-cur-input app-state complete-placeholder)
                         :else
                         (swap! app-state assoc :cur-index (mod (inc cur-index) (count filtered-items))))
                       (.call (aget % "preventDefault") %))
                     identity)}]
         [:input {:style {:width "100%" :height "1.9em" :padding-left "10px" :color "gray" :position "absolute" :top "0" :right "0" :left "0" :bottom "0" :z-index "-1"
                          :background "white"} :disabled true
                  :placeholder complete-placeholder}]
         [:div {:style {:position "absolute" :right "0" :left "0" :max-height "200px" :max-width "600px" :top "2.3em" :box-shadow "grey 1px 2px 1px 0px" :background "white" :overflow "hidden" :padding-top "10px" :padding-bottom "10px" :padding-left "10px" :border "none"}}
          (for [item (take display-size filtered-items)]
            ^{:key (:name item)}
            [:div {:on-click #(submit-fn (:name item))}
             [render-fn cur-input (:selected? item) (:name item)]])]]))))


