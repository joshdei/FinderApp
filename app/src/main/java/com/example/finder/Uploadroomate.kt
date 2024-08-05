import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Uploadroomate(
    val userid: String = "",
    val title: String = "",
    val numberOfGuest: String = "",
    val address: String = "",
    val pricePerNight: String = "",
    val availableDate: String = "",
    val date: String = "",
    val description: String = "",
    val imageUri: String = ""
) : Parcelable
