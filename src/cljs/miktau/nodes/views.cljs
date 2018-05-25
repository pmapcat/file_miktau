(ns miktau.nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]
            [miktau.tools :as utils]))

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))


(defn radio-button
  [text on-change selected?]
  [:label.pure-checkbox
   {:for "blab" :style {:position "relative"}}
   [:input
    {:id "blab"  :style {:width "25px" :height "25px" :cursor "pointer"} :checked selected? :type "checkbox" :on-change on-change}]
   [:span {:style {:padding-bottom "5px"}}
    text]])

(defn breadcrumbs []
  (let [breadcrumbs @(refe/subscribe [:nodes/breadcrumbs])]
    [:div 
     [:a.unstyled-link.red-clickable {:href "#" :on-click #(refe/dispatch [:nodes/clear]) :style {:padding-right "5px"}} "«Clear»"]
     
     [:span {:style {:padding-right "5px"}}
      
      ;; calendar
      (for [[item meta-item]  (for-every-and-last (:calendar breadcrumbs))]
        ^{:key (:name item)}
        [:span [:a.unstyled-link.black-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
         (if-not (:last? meta-item) " > " " | ")])
      
      ;; cloud items
      (for [[item meta-item] (for-every-and-last (:cloud-items breadcrumbs))]
        ^{:key (:name item)}
        [:span [:a.unstyled-link.black-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
         (if-not (:last? meta-item) " > " " ")])]
     
     ;; potential selection
     (if-not (empty? (:cloud-can-select breadcrumbs))
       [:span
        "( "
        (for [[item meta-item]  (for-every-and-last
                                 (if (:show-all? breadcrumbs)
                                   (:cloud-can-select breadcrumbs)
                                   (take 10 (:cloud-can-select breadcrumbs))))]
          
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:href "#" :on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        
        (if (:can-expand? breadcrumbs)
          [:a.unstyled-link.green-clickable {:href "#" :style {:padding-left "5px" } :on-click #(refe/dispatch [:nodes/breadcrumbs-show-all?-switch])} "…" ]
          [:span])
        " )"]
       [:span])]))

(defn- sortable-header [order-by]
  [:span
   (:name order-by) [:br]
   [:span {:style {:font-size "0.6em"}}
    "[ "
    (for [[_ point] (:items order-by)]
      [:a.unstyled-link
       {:href "#" :key (:name point) :on-click #(refe/dispatch (:on-click point)) :class (if (:enabled? point) " green-disabled " " black-clickable ")} (:name point) " "]) "]"]])

(defn file-table-header [all-selected? order-by]
  [:tr
   ;; select all nodes button
   [:th.mik-flush-left
    ;; [radio-button "" #(refe/dispatch [:nodes/select-all-nodes]) all-selected?]
    ]
   
   [:th.mik-flush-left
    [sortable-header (:name order-by)]]
   [:th.mik-flush-left.padded-as-button
    [:span
     "Showing: " [:b 3217 " results "] [:br]]
    [:span {:style {:font-size "0.6em"}}
     "Selected: " [:b 0] " "
     [:a.unstyled-link.blue-clickable {:href "#" :on-click #(refe/dispatch [:nodes/select-all-nodes])} " Select all "]]]
   [:th.mik-flush-right
    [sortable-header (:modified order-by)]]])

(defn tagging-in-a-single-node-item
  [tags]
  [:span
   (for [[tag meta-tag] (for-every-and-last tags)]
     ^{:key (:key-name tag)}
     [:span
      [:a.tag
       {:on-click #(refe/dispatch [:nodes/clicked-cloud-item (tag :key-name)])
        :class (cond
                 (:same-as-prev? tag) " almost-hidden-in-plain-sight "
                 (:selected? tag) "selected"
                 (:can-select? tag) "can-select"
                 :else "disabled")
        :style {:pointer "cursor"}}
       (:name tag)]
      [:span {:class (if  (:same-as-prev? tag) " almost-hidden-in-plain-sight " "")}
       (if-not (:last? meta-tag) "»" "")]])])

(defn single-node-item [node]
  [:tr
   {:key (str  (:id node))
    :data-fpath  (str (:file-path node) (:name node))
    :style {:font-size "0.8em" :border-bottom "solid 1px #e3e3e3" :cursor "pointer"}
    :class (if (:selected? node) " node-selected " "")
    }
   [:td
    [:div {:style { :padding-left "10px"}}
     [radio-button "" #(refe/dispatch [:nodes/select-node (str (node :file-path) (node :name))]) (:selected? node)]]]
   [:td
    [:a.unstyled-link.black-clickable 
     {:href "#" :style {:font-weight "300" :word-wrap "break-word"}}
     (:name node)]]
   [:td
    [tagging-in-a-single-node-item (:tags node)]
    (if (and  (not  (empty? (:all-tags node))) )
      [:a.unstyled-link.black-clickable
       {:href "#"
        :class (if  (:all-tags-repeat-as-prev? node) " almost-hidden-in-plain-sight " "")
        :on-click #(refe/dispatch [:nodes/clicked-many-cloud-items (:all-tags node)])}
       [:span
        {:style {:padding "3px" :margin "3px" :font-size "0.8em"}}
        [views-utils/icon "arrow_forward"]]]
      [:span])]
   [:td.mik-flush-right
    [:a.unstyled-link.blue-clickable
     {:href "#"
      :class (if (:modified-as-prev? node)
               " almost-hidden-in-plain-sight "
               "")
      :on-click
      #(refe/dispatch  [:nodes/click-on-fast-access-item (node :modified)])
      :style {:font-weight "300"}}
     
     (str
      (utils/pad (:year   (node :modified)) 4 "0") "."
      (utils/pad (:month  (node :modified)) 2 "0") "."
      (utils/pad (:day    (node :modified)) 2 "0"))]]])
(defn pagination
  [paginator]
  [:div.mik-flush-right {:style {:font-size "0.5em"}}
   (for [page  (:pages paginator)]
     ^{:key (if (:fedots? page) "first_dots" (:name page))}
     [:div.pure-button {:on-click #(refe/dispatch (:on-click page)) :class
                        (str
                         (if-not (:active? page) " pure-button-disabled " "")
                         (if (:current? page) " pure-button-primary " ""))
                        } (:name page)])])

(defn file-table []
  (let [node-items  @(refe/subscribe [:nodes/node-items])
        order-by    @(refe/subscribe [:nodes/order-by])
        paginate    @(refe/subscribe [:nodes/pagination])]
    ;; (if selection-mode?
    ;;   [:button.pure-button
    ;;    {:on-click #(refe/dispatch [:nodes/edit-nodes])} "Edit tags"]
    ;;   [:span])
    
    [:table {:style {:width "100%"}}
     [:thead
      [file-table-header  (:all-selected? node-items) order-by]]
     [:tbody
      (for [node (:nodes node-items)]
        ^{:key (str (:id node))}
        [single-node-item node])]
     [:tfoot
      [:tr [:td] [:td] [:td]
       [:td
        [:div {:style {:margin-top "1em"}}
         [pagination paginate]]]]]]))

(defn main
  []
  [:div.pure-g
   [:div.pure-u-1.padded-as-button
    [file-table]]])




