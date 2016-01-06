(ns ^:figwheel-always mystic.components
  (:require
   [rum.core :as rum]
   [mystic.core :as core :refer [model]]
   ))

(def min-nodes 1)
(def max-nodes 25)
(def mystic-r 190)
(def mystic-o {:x 200 :y 200})

(defn mean [a b]
  (/ (+ a b) 2))

(defn dot-coord [key theta]
  (+ (key mystic-o) (* mystic-r ((if (= key :x) Math.cos Math.sin) theta))))

(defn i->theta [n i]
  (/ (* 2 Math.PI i) n))

(defn thetas [n]
  (map #(i->theta n %) (range n)))

(rum/defc start-button [handler]
  [:button.start.btn-default.btn-left
   {:on-click handler
    :on-touch-start handler} "Start"])

(rum/defc reset-button [handler]
  [:button.start.btn-default.btn-right
   {:on-click handler
    :on-touch-start handler} "Reset"])

(rum/defc dot [theta]
  [:circle {:stroke "#ffffff" :stoke-width 5 :fill "#CCCCCC" :r 4
            :cx (dot-coord :x theta)
            :cy (dot-coord :y theta)}])

(rum/defc n-slider < rum/cursored [mystic-n]
  [:input {:type "range" :value @mystic-n :min min-nodes :max max-nodes
           :style {:width "100%"}
           :on-change #(swap! core/model assoc
                              :mystic-n (-> % .-target .-value))}])

(rum/defc t-slider < rum/cursored [mystic-t]
  [:input {:type "range" :value @mystic-t :min 0 :max 100
           :style {:width "100%"}
           :on-change #(swap! core/model assoc
                              :mystic-t (/ (-> % .-target .-value) 100))}])

(rum/defc count-input < rum/cursored [mystic-n]
  [:span.node-count
   [:span
    [:input.inp-default
     {:type "number"
      :min min-nodes :max max-nodes :value @mystic-n
      :on-change #(swap! core/model assoc :mystic-n (.parseInt js/Number (-> % .-target .-value)))}]
    "points"]
   (n-slider (rum/cursor model [:mystic-n]))])

(rum/defc dots-on-circle < rum/cursored [mystic-n]
  [:g
   (map-indexed  #(rum/with-key (dot %2) %1) (thetas @mystic-n))])

(rum/defc semi-chord [theta1 theta2 t]
  [:line {:x1 (dot-coord :x theta1)
          :y1 (dot-coord :y theta1)
          :x2 (+ (* (dot-coord :x theta1) (- 1 t)) (* (dot-coord :x theta2) t))
          :y2 (+ (* (dot-coord :y theta1) (- 1 t)) (* (dot-coord :y theta2) t))
          :stroke "rgba(0,0,0,0.2)"
          :stroke-width 1
          }])

(rum/defc chords [mystic-n]
  (let [mystic-thetas (thetas @mystic-n)]
    [:g
     (for [theta1 mystic-thetas
           theta2 mystic-thetas]
       (rum/with-key (semi-chord theta1 theta2 0.5) [theta1 theta2]))])
  )

(rum/defc method-1-chords < rum/cursored [mystic-n mystic-t]
  (let [t (/ @mystic-t 2)
        mystic-thetas (thetas @mystic-n)]
    [:g
     (for [theta1 mystic-thetas
           theta2 mystic-thetas]
       (rum/with-key (semi-chord theta1 theta2 t) [theta1 theta2]))])
  )

(rum/defc method-2-chords < rum/cursored [mystic-n mystic-t]
  (let [t @mystic-t]
    [:g
     (for [i (range @mystic-n)
           j (range i)]
       (rum/with-key (semi-chord (i->theta @mystic-n i) (i->theta @mystic-n j) t) [i j]))])
  )

(rum/defc select-method < rum/cursored [method]
  [:#methods
   [:span
    [:label {:for "method-sel"} "Draw using method "]
    [:select#method-sel {:value @method
                         :on-change #(swap! core/model assoc :method (.parseInt js/Number (-> % .-target .-value)))}
     [:option {:value 1} 1]
     [:option {:value 2} 2]]]
   (t-slider (rum/cursor core/model [:mystic-t]))])

(rum/defc basic-mystic-rose < rum/cursored rum/cursored-watch [model]
  [:div
   #_(start-button #(prn "start"))
   #_(reset-button #(prn "reset"))
   #_(select-method (rum/cursor model [:method]))
   (count-input (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (chords (rum/cursor model [:mystic-n]))
     ]]
   [:p (str @model)]
])

(rum/defc mystic-rose-1 < rum/cursored rum/cursored-watch [model]
  [:div
   #_(start-button #(prn "start"))
   #_(reset-button #(prn "reset"))
   (select-method (rum/cursor model [:method]))
   (count-input (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (method-1-chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]))
     ]]
   [:p (str @model)]
])

(rum/defc mystic-rose-2 < rum/cursored rum/cursored-watch [model]
  [:div
   #_(start-button #(prn "start"))
   #_(reset-button #(prn "reset"))
   (select-method (rum/cursor model [:method]))
   (count-input (rum/cursor model [:mystic-n]))
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 2 :cx 200 :cy 200 :r 190}]
     (dots-on-circle (rum/cursor model [:mystic-n]))
     (method-2-chords (rum/cursor model [:mystic-n]) (rum/cursor model [:mystic-t]))
     ]]
   [:p (str @model)]
])
