(ns mystic.components
  (:require
   [rum.core :as rum]
   ))

(rum/defc start-button [handler]
  [:button.start.btn-default.btn-left
   {:on-click handler
    :on-touch-start handler} "Start"])

(rum/defc reset-button [handler]
  [:button.start.btn-default.btn-right
   {:on-click handler
    :on-touch-start handler} "Reset"])

(rum/defc dot [r theta]
  [:circle {:stroke "white" :stoke-width 3 :fill black :r 3
            :cx (+ 200 (* r (Math.cos theta)))
            :cy (+ 200 (* r (Math.sin theta)))}])


(rum/defc mystic-rose []
  [:div
   (start-button #(prn "start"))
   (reset-button #(prn "reset"))
   [:input.inp-default {:type "number" :min 5 :max 100}]
   [:svg {:view-box "0 0 400 400"}
    [:g
     [:circle.outlined {:fill "none" :stroke "black" :stroke-width 3 :cx 200 :cy 200 :r 190}]
     [:g
      (dots 10)]
     ]]])
