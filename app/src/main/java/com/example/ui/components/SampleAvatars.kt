package com.example.ui.components

data class AvatarOption(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val gender: String,
    val suggestedCity: String,
    val suggestedSchool: String
)

object SampleAvatars {
    val list = listOf(
        AvatarOption(
            id = "avatar_01",
            name = "Sarah Jenkins",
            description = "Creative Designer (Female, mid-20s, smiling, outdoor warm bokeh)",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300",
            gender = "Female",
            suggestedCity = "San Francisco, CA",
            suggestedSchool = "Stanford University"
        ),
        AvatarOption(
            id = "avatar_02",
            name = "David Chen",
            description = "Software Developer (Male, late-20s, wearing glasses, indoor workspace)",
            imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300",
            gender = "Male",
            suggestedCity = "Seattle, WA",
            suggestedSchool = "UW Informatics"
        ),
        AvatarOption(
            id = "avatar_03",
            name = "Clarissa Vance",
            description = "Digital Marketer (Female, early-30s, casual blazer, metropolitan sunset)",
            imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=300",
            gender = "Female",
            suggestedCity = "New York, NY",
            suggestedSchool = "NYU Stern"
        ),
        AvatarOption(
            id = "avatar_04",
            name = "Marcus Ridley",
            description = "Fitness Trainer (Male, mid-20s, athletic attire, sunny stadium)",
            imageUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&q=80&w=300",
            gender = "Male",
            suggestedCity = "Austin, TX",
            suggestedSchool = "UT Austin Kinesiology"
        ),
        AvatarOption(
            id = "avatar_05",
            name = "Elena Rostova",
            description = "Product Lead (Female, late-20s, elegant glasses, modern library)",
            imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&q=80&w=300",
            gender = "Female",
            suggestedCity = "Boston, MA",
            suggestedSchool = "Harvard Business School"
        )
    )
}
