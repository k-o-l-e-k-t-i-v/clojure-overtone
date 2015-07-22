;;
;;     MM""""""""`M
;;     MM  mmmmmmmM
;;     M`      MMMM 88d8b.d8b. .d8888b. .d8888b. .d8888b.
;;     MM  MMMMMMMM 88''88'`88 88'  `88 88'  `"" Y8ooooo.
;;     MM  MMMMMMMM 88  88  88 88.  .88 88.  ...       88
;;     MM        .M dP  dP  dP `88888P8 '88888P' '88888P'
;;     MMMMMMMMMMMM
;;
;;         M""MMMMMMMM M""M M""MMMMM""M MM""""""""`M
;;         M  MMMMMMMM M  M M  MMMMM  M MM  mmmmmmmM
;;         M  MMMMMMMM M  M M  MMMMP  M M`      MMMM
;;         M  MMMMMMMM M  M M  MMMM' .M MM  MMMMMMMM
;;         M  MMMMMMMM M  M M  MMP' .MM MM  MMMMMMMM
;;         M         M M  M M     .dMMM MM        .M
;;         MMMMMMMMMMM MMMM MMMMMMMMMMM MMMMMMMMMMMM  Version 1.0beta24
;;
;;           http://github.com/overtone/emacs-live
;;
;; Jr, turn your head towards the sun and the shadows will fall behind you.


(defsynth tick
  [rate 4
   outa 0
   outb 1]
  (let [t (impulse:kr rate)
        c (pulse-count t 0)]

    (out outa t)
    (out outb c)))

(kill bus-tick)
(do
  (def bus-tick (control-bus 1))
  (def bus-cnt (control-bus 1))
  (def master-tick (tick 4 bus-tick bus-cnt))
  (def k1 (k :inb bus-tick :diva 2 :fb 40)))

(def bt (control-bus 1))
(def mt1 (tick 4 bt bus-cnt))
(def k2 (k :inb bus-tick :fb 80))
(def k3 (k :inb bus-tick :diva 4 :fb 90))
(def k4 (k :inb bus-tick :diva 4 :fb 190))
(def k5 (k :inb bus-tick :diva 6 :fb 290))
(def k5 (k :inb bus-tick :diva 3 :fb 390))
(def k7 (k :inb bus-tick :diva 12 :fb 590))
(def k8 (k :inb bus-tick :diva 14 :fb 790))
(def k9 (k :inb bus-tick :diva 15 :fb 1790))
(inst-mixer)
(inst-fx!)
;;(kill k1)
(odoc inst-mixer)

(definst k
  [inb 0
   diva 0
   fb  40]
  (let [t (in:kr inb)
        t (pulse-divider t diva)
        ae (env-gen (perc 0 0.2 1 -10) t  1 0 1)
        fe (env-gen (perc 0 0.1 1000 -10) t 1 fb 1)
        s  (sin-osc fe)
        sa (* s ae)]
    (out 0 (pan2 sa))))

(stop)

(definst robo
  [inb 0
   r   0.1]
  (let [i (in:ar inb)
        m (comb-c i 1 r 0.9)
        r (free-verb m
                     0.1
                     0.5
                     0.1)]
    (replace-out 0 r)))

(def r1 (robo :inb 0))
(on-event "/tr" (fn [x]
                  (ctl r1 :r (choose [0.125 0.5 0.25 0.0625 1/16 1/32]))) "roboe")
(definst gater
  [inb 0
   r 4
   w 0.5]
  (let [i (in:ar inb)
        m (* i (pulse:ar r w))]
    (replace-out 0 m)))

(def g1 (gater :inb 0))

(on-event "/tr" (fn [x]
                  (ctl g1 :r (choose [4 6 3 2 8 1]))
                  (ctl g1 :w (choose [0.125 0.5 0.25 0.75]))) "gat")
(ctl master-tick :rate 2)

(odoc pulse)

(def r2 (robo :inb 0))

(on-event "/tr" (fn [x]
                  (ctl r2 :r (choose [1/4 1/5 1/8 1/7 1/12]))) "roboe2")



(definst
  room
  [inb 0
   m   0.4
   r   0.8
   d   0.1]
  (let [i (in:ar inb)
        re (free-verb i
                      m
                      r
                      d)
        p (lf-noise2 1)
        ]
    (out 0 (pan2 re p))))
(def room1 (room))

(ctl room1 :m 0.1)

(ctl master-tick :rate 1)

(stop)
