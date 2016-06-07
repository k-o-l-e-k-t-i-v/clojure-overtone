(defsynth time-machine
  [tbus 0
   cbus 0
   rate 0]
  (let [t (impulse:kr rate)
        c (pulse-count:kr t)]
    (out tbus t)
    (out cbus c)))
(do
  (defonce tbus1 (control-bus 1))
  (defonce cbus1 (control-bus 1))
  (def tm1 (time-machine :tbus tbus1
                         :cbus cbus1))
  )

(defsynth kicka
  [cbus 0
   cmod 2
   cdiv 1
   coff 0
   slen 20
   ]
  (let [
        ;; master envelope of synth
        m (env-gen (envelope [0 1 1 0] [4 (- slen 8) 4]) 1 1 0 1 FREE)

        ;; trigger processing from counting input bus
        ti (in:kr cbus)
        ti (= 0 (mod ti cmod))
        ti (pulse-divider ti cdiv coff)

        ;; patterns
        p1 (demand ti 0 (dseq [1 0 0 1 0 0 1 0 0 1 0 1 1 0 0 1 0] INF))
        p2 (demand p1 0 (dseq [1900 400 1300 400 300 1500 2000 300] INF))
        p3 (demand p1 0 (dseq [1 0 1 0 0 0 1 0 0 1] INF))
        p4 (demand p1 0 (dseq [0 0 0 1 0 0 0 1 0 1 0 0 0 0 0 1] INF))
        p5 (demand p3 0 (dseq [0 0 0 0 1 0 0 1 0 0 1 0 0 0 0 1 0 0 1 0 0] INF))
        ;; envelopes
        fe (env-gen (perc 0 0.03 1 -10) p1 p2 40 1)
        ae (env-gen (perc 0 0.3 1 -10) p1 1 0 1)

        ;; sounds
        s1 (sin-osc-fb fe 0.4)
        s  (* s1 ae)

        ;;fxs
        fx1 (comb-n (hpf  (* p3 s) 2000) 2 1/8 0.49)
        fx2 (comb-c (bpf (* p4 s) 800) 2 1/16 0.6)
        fx3 (comb-l (rlpf (* p5 s) 1000 1.2)
                    2 1/2 0.7)


        s (mix [s fx1 fx2 fx3])
        s (wrap:ar s -0.15 0.15)
        s (g-verb s 100 4 0.7)
        s (normalizer s)
        s (* m s)
        ]
    (out 0 (pan2 s 0 0.2))))


(kill kicka)
(do ;(ctl tm1 :rate 32)
  (def k1 (kicka :cbus cbus1 :cmod 2 :cdiv 2 :slen 100))
  (def k2 (kicka :cbus cbus1 :cmod 4 :cdiv 4 :slen 100)))
