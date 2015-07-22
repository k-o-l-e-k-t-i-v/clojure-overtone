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




(def rb-1 (control-bus 1))
(def rb-2 (control-bus 1))
(def rb-3 (control-bus 1))

(defsynth rs
  [outbus 0
   rate   16]
  (let [t (impulse:kr rate)]
    (out outbus t)))

(def rs-1 (rs rb-1 16))
(def rs-2 (rs rb-2 12))


(defsynth kick
  [cbus      0
   freq-base 40
   freq-mod  400
   div       4
   g         1
   amp       1]
  (let [t  (in:kr cbus)
        td (pulse-divider:kr t div 0)
        a  (env-gen (adsr 1 0 1 1 amp 0 0) g 1 0 1 FREE)
        fa (env-gen (perc 0 0.3 freq-mod -10) td 1 freq-base 1)
        aa (env-gen (perc 0 0.3 amp 0) td 1 0 1)
        s  (sin-osc-fb fa 0.5)
        sa (* s aa)
        sout (* sa a)
        ]
    (out 0 (pan2 sout 0 1))))

(def kick-1 (kick rb-1))
(do
  (ctl kick-3 :g 0)
  (def kick-4 (kick rb-1 :div 8 :freq-base 80)))


(def kick-5 (kick rb-1 :div 4 :freq-base 40))
(def kick-6 (kick rb-2 :div 12 :freq-mod -800 :freq-base 120))
(def kick-7 (kick rb-2 :div 18 :freq-mod -800 :freq-base 220))
(ctl kick-5 :g 0)

(ctl kick-6 :div 10)
(ctl kick-5 :div 4)

(ctl kick-7 :freq-base 20)
(ctl kick-6 :freq-base 20 :div 12)
(ctl kick-6 :freq-mod -200)
(demo 60
      (replace-out [0 1] [ (normalizer  (mix [ (comb-c  (in:ar 0)
                                                        4
                                                        (lag  (demand (impulse 1) 0 (drand [1/32 1/64 1/24 1/8 1/2] INF))
                                                              0.5)
                                                        0.5)
                                               (in:ar 1)]))
                           (normalizer  (mix [ (comb-c  (in:ar 0)
                                                        4
                                                        (lag  (demand (impulse 1) 0 (drand [1/32 1/64 1/24 1/8 1/2] INF))
                                                              0.5)
                                                        0.5)
                                               (in:ar 1)]))]))
(stop)
