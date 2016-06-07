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

(def metro-1 (metronome (* 3 160)))
(def p1 (ref [1 0 0 0 1 0 0 1 0 0]))
(def p2 (ref [1 0 0 1 0 0 1 0 1 0]))

(stop)                                        ;(zero-beat metro-1 6)
(let [zb (zero-beat metro-1 8)]
  (at (metro-1 zb)
      (seq-player metro-1 zb @p1 p1 #'bd)
      (seq-player metro-1 zb @p2 p2 hat)
      (seq-player metro-1 zb @chpat-1 chpat-1 ch1)
      ))

(def chpat-1 (ref  [1 0 0 0 0 0 0 0
                    0 0 0 0 0 0 0 0
                    0 0 0 0 0 0 0 0
                    0 0 0 0 0 0 0 0]))

(dosync (ref-set chpat-1 [1 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0]))
(change-pat p1)
(defn ch1 []
  (change-pat p1))
(defn change-pat [x]
  (let [p @x
        c (count p)
        p2 (rotate (rand-int c)
                   p)]
    (dosync (ref-set x p2))))

(dosync (ref-set p1 [1 0 1 0
                     0 1 0 1
                     1 0]))
(dosync (ref-set p2 [1 0 1 0 1 0 1 1
                     1 0 1 ]))

(metro-bpm metro-1 (* 3 160))
(stop)

(dosync (ref-set p1 (rotate 2 @p1)))
(dosync (ref-set p1 (cons @p1 (take (rand-int (count @p1)) @p2))))

(stop)
(defsynth comb-fx
  [rate 0
   g 0
   inbus 0
   d 1]
  (let [me (env-gen (asr 1 1 1 0) g 1 0 1)
        i (in:ar inbus 1)
        i (bpf i 2000 1)
        s (comb-c i 2 (lag rate 1) d)
        s (freq-shift s 500)
        sme (* s me)]
    (out 0 (pan2 sme 0 1))))

(let [zb (zero-beat metro-1 8)]
  (at (metro-1 zb)
      (def fx-1 (comb-fx [:head fxgroup]
                         (* 0.0005 (metro-tock metro-1))
                         0
                         0
                         1))))


(let [r (metro-tock metro-1)
      t (* 0.001 r)
      ra (choose [4 8 12])
      rat (* t ra)]
  (ctl fx-1 :g 1 :rate rat))

(ctl fx-1 :g 0)
(kill fx-1)
(def sources (group))
(def fxgroup (group :after sources))
(def dist-pat-1 (ref [20 5 5 5]))
(dosync (ref-set dist-pat-1 [200 120 10 50 5]))
(def hatl-pat-1 (ref [0.1 0.075 0.133 0.14 0.05 0.17]))
(defn hat []
  (let [d (first @dist-pat-1)
        l (first @hatl-pat-1)]
    (hatboom [:head sources]
             :ml l
             :md d)
    (dosync (ref-set dist-pat-1 (rotate 1 @dist-pat-1)))
    (dosync (ref-set hatl-pat-1 (rotate 1 @hatl-pat-1))))
  )
(def bdm (ref [900 130 90 40 120 40 40]))
(dosync (ref-set bdm [200 50 50 50 50]))
(defn bd []
  (let [m (first @bdm)]
    (boom [:head sources]
          :freq-mod (choose [2000 9000 200])
          :freq-base m)
    (dosync (ref-set bdm (rotate 1 @bdm))))
;  (hatboom [:head sources])
  )
(stop)
(acid-change)

(let [zb (zero-beat metro-1 8)]
  (at (metro-1 zb)
      (def fx-2 (comb-fx [:head fxgroup]
                         (* 0.0005 (metro-tock metro-1))
                         0
                         0
                         1))))

(let [r (metro-tock metro-1)
      t (* 0.001 r)
      ra (choose [1 2 3 4 6 7 8 9])
      rat (* t ra)]
  (ctl fx-2 :g 1 :rate rat))
(kill fx-2)
(stop)

(defsynth acid-bass
  [notein 60
   res    0.5
   flt-m  0
   flt-b  1000
   sglide 0
   fglide 0
   g      1]
  (let [me (env-gen (asr 1 1 1 0) g 1 0 1)

        sf (lag (midicps notein) sglide)
        ff (lag (+ flt-m flt-b) fglide)

        s (mix [(saw sf)
                (saw [sf (* 2.03 sf)])
                (saw [sf (* 0.5 sf)])])
        s (rlpf s ff res)

        s (freq-shift s -1000 (sin-osc-fb sf 1))

        sfx (g-verb s 200 1.9 0.63)

        s (mix [(* 0.13 s) sfx])
        s (normalizer s 1)
        sme (* s me)
        ]
    (out 0 (pan2 sme 0 1))))

(def ab-1 (acid-bass :notein (note :C2)))
(kill acid-bass)
(stop)
(def acid-pat-1 (ref [1 1 0 1 0 1 1 1 0]))
(def acid-mel (ref [:C2 :d2 :G2 :D2
                    :G3 :C3 :C#3 :A2]))

(dosync (ref-set acid-mel [:C5 :E6 :F3 :D#5 :C3 :D4 :E6 :F#6 :G#3]))
(dosync (ref-set acid-mel [:C4 :G4 :F4 :G5 :C6 :C3]))
(stop)
(defn acid-change []
  (let [c (first @acid-pat-1)
        n (first @acid-mel)]
    (if (= 1 c)
      (do  (ctl ab-1 :notein (- (note n) (choose [0];[7 14 21 -14 28]
                                          ))
                :flt-m (choose [0] ;[1000 100 2000 100 3000]
                               )
                :fglide (choose [0.01 0.12 0.03 0.2])
                :flt-b (midi->hz (note n))
                :sglide (choose [0.01 0.02 0.25 0]))

           ))
    (dosync (ref-set acid-mel (rotate 1 @acid-mel)))
    (dosync (ref-set acid-pat-1 (rotate 1 @acid-pat-1)))))

(acid-change)
(kill ab-1)

(stop)

(let [zb (zero-beat metro-1 8)]
  (at (metro-1 zb)
      (do (seq-player metro-1 zb @acid-pat-1 acid-pat-1 #'acid-change)
          (seq-player metro-1 zb @p1 p1 #'bd)
          (seq-player metro-1 zb @p2 p2 #'hat)
          (seq-player metro-1 zb @chpat-1 chpat-1 #'ch1)
          )))

(stop)

(do
  (dosync (ref-set acid-pat-1 [1 0 1 1 0
                               1 1 0 0 1 0
                               1 0 1 0
                               ]))
  (dosync (ref-set p1 [1 1 0 1 0 0 1 0 0 1 0]))
  (dosync (ref-set p2 [1 0 1 1 0 1 0 1 1 0 1 1 1 0 1 0]))
  (dosync (ref-set chpat-1 [0 0 0 0
                            0 0 0 0
                            0 0 0 0
                            0 0 0 0
                            0 0 0 0
                            0 0 0 0
                            ])))
(defn tempo-change []
  (metro-bpm metro-1 (* 160  (choose [3]);(choose [1 2 0.5 2/3 4/3])
                        )))

(tempo-change)
























(stop)

(def tpat-1 (ref [1 0 0 0 0 0 0 0
                  0 0 0 0 1 0 0 0]))
(metro-bpm metro-1 (* 4 160))

(let [zb (zero-beat metro-1 8)]
  (at (metro-1 zb)
      (do (seq-player metro-1 zb @tpat-1 tpat-1 #'tempo-change)
          )))
