package com.transit.gramayatri.data.model

data class busRoute(
    val busId: String,
    val displayName: String,
    val stops: List<String>,
    val avgTimesMinutes: List<Int>
)

object Routes {

    val all: List<busRoute> = listOf(

        busRoute(
            busId = "70L",
            displayName = "70L — Simhachalam → Vijaya Nagaram",
            stops = listOf(
                "Simhachalam",
                "Advivaram",
                "Sontium",
                "Gajapati Nagaram",
                "Padhmanabam",
                "Viti Agraharam",
                "Vijaya Nagaram"
            ),
            avgTimesMinutes = listOf(5, 6, 7, 8, 6, 7)
        ),

        busRoute(
            busId = "83P",
            displayName = "83P — Simhapuri Colony → Vijaya Nagaram",
            stops = listOf(
                "Simhapuri Colony",
                "Old Goshala",
                "Govt Hospital",
                "Simhachalam Hilltop",
                "Simhachalam",
                "Advivaram",
                "Sontium",
                "Gajapati Nagaram",
                "Padhmanabam",
                "Viti Agraharam",
                "Vijaya Nagaram"
            ),
            avgTimesMinutes = listOf(4, 5, 6, 5, 5, 6, 7, 8, 6, 7)
        ),

        busRoute(
            busId = "43N",
            displayName = "43N — Bus Depot → Gandsi",
            stops = listOf(
                "Bus Depot",
                "NR Circle",
                "Diary Circle",
                "Dudda",
                "Heragu",
                "Attavaru Gate",
                "Gandsi"
            ),
            avgTimesMinutes = listOf(5, 6, 8, 7, 6, 8)
        ),

        busRoute(
            busId = "12R",
            displayName = "12R — Railway Station → Vijaya Nagaram",
            stops = listOf(
                "Railway Station",
                "RTC Complex",
                "Rama Talkies",
                "Dr. LB Womens College",
                "Swarna Bharthi Stadium",
                "Maddhilapalam",
                "Isakathota",
                "Venkojipalam",
                "Hanumanathavaka",
                "Old Dairy Farm",
                "Zoopark",
                "Vizag Stadium",
                "PM Pallam",
                "Madhurvada",
                "Kommadhi Village",
                "Anadhapuram",
                "ANITS College",
                "Lendi College",
                "Vijaya Nagaram"
            ),
            avgTimesMinutes = listOf(4, 5, 6, 5, 7, 6, 5, 6, 5, 7, 6, 5, 8, 7, 6, 5, 6, 7)
        ),

        busRoute(
            busId = "68K",
            displayName = "68K — Kottavalasa → Dhabba Gardens",
            stops = listOf(
                "Kottavalasa Bus Stand",
                "Kottavalasa Railway Station",
                "Bodipalli",
                "Vizag Waterworld",
                "Saripalli",
                "Pendhurti College",
                "Sujatha Nagar",
                "Puroshothapuram",
                "Vapakunta",
                "Appanapalam",
                "Simhapuri Colony",
                "Old Goshala",
                "Govt Hospital",
                "Simhachalam",
                "Pineapple Colony",
                "Ramakrishanapuram",
                "Central Jail",
                "Mudharas Slova",
                "Arilova Medcity",
                "Pinnacle Hospital",
                "Sai Baba Temple",
                "Narayana Medcity",
                "VIMS Hospital",
                "Dhabba Gardens"
            ),
            avgTimesMinutes = listOf(5,6,7,6,7,5,6,5,6,5,4,5,6,5,6,5,7,6,5,6,5,6,7)
        ),

        busRoute(
            busId = "79E",
            displayName = "79E — Hanumanthavaka → RTC Complex",
            stops = listOf(
                "Hanumanthavaka",
                "Venkojipalam",
                "Isakathota",
                "Maddhipalam",
                "Swarna Bharthi Stadium",
                "Dr. LB Womens College",
                "Rama Talkies",
                "RTC Complex"
            ),
            avgTimesMinutes = listOf(6, 5, 7, 6, 5, 6, 5)
        ),

        busRoute(
            busId = "53A",
            displayName = "53A — Chatram Bus Stand → Police Colony",
            stops = listOf(
                "Chatram Bus Stand",
                "Palakkarai",
                "Gandhi Market",
                "Railway Junction",
                "Central Bus Stand",
                "TVS Tollgate",
                "Airport",
                "Gundur",
                "Police Colony"
            ),
            avgTimesMinutes = listOf(8, 7, 6, 5, 8, 10, 7, 6)
        ),

        busRoute(
            busId = "22I",
            displayName = "22I — Gita Mandir → Navagam",
            stops = listOf(
                "Gita Mandir",
                "Paldi",
                "Bareja",
                "Nadiad",
                "Anand",
                "Central Depot",
                "Dabhoi",
                "Rajpipla",
                "Navagam"
            ),
            avgTimesMinutes = listOf(10, 12, 15, 14, 10, 16, 18, 12)
        ),

        busRoute(
            busId = "15B",
            displayName = "15B — Ganeshpeth → Salaidhaba",
            stops = listOf(
                "Ganeshpeth Bus Stand",
                "Hingna T Point",
                "Lokmanya Nagar",
                "Rajiv Nagar",
                "Wanadongri",
                "Hingna Gaon",
                "Mondha Gaon",
                "Gittikhadan",
                "Sukdi",
                "Salaidhaba"
            ),
            avgTimesMinutes = listOf(8, 6, 5, 7, 6, 5, 6, 7, 8)
        )
    )
    fun findById(busId: String): busRoute? = all.find {
        it.busId.equals(busId, ignoreCase = true)
    }
}