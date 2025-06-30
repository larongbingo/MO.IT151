using System.Collections.ObjectModel;
using Android.Content.OM;
using CommunityToolkit.Maui.Alerts;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using MOIT151.Mobile.Services;
using NewRelic.MAUI.Plugin;
using File = MOIT151.Core.File;

namespace MOIT151.Mobile.Pages.Files;

public partial class FilesViewModel(IAuthenticationService authnService, IMoit151WebClient webClient) : ObservableObject
{
    [ObservableProperty] private bool isFilesLoading = true;
    [ObservableProperty] private ObservableCollection<File> files = [];
    [ObservableProperty] private File? selectedFile;
    
    [RelayCommand]
    private async Task LoadFilesAsync(CancellationToken ct = default)
    {
        CrossNewRelic.Current.RecordMetric("File Query", "File");
        var files = await webClient.GetFilesAsync(authnService.AccessToken, ct);
        Files = new ObservableCollection<File>(files);
        IsFilesLoading = false;
    }

    [RelayCommand]
    private async Task DownloadFileAsync(CancellationToken ct = default)
    {
        var interactionId = CrossNewRelic.Current.StartInteraction("DownloadFile");
        if (SelectedFile is null)
            return;

        var url = await webClient.GetDownloadUrlAsync(authnService.AccessToken, SelectedFile.Id, ct);
        await Browser.OpenAsync(url.Uri, BrowserLaunchMode.External);
        CrossNewRelic.Current.EndInteraction(interactionId);
        CrossNewRelic.Current.RecordMetric("Download", "File");
    }
    
    [RelayCommand]
    private async Task UploadFileAsync(CancellationToken ct = default)
    {
        var interactionId = CrossNewRelic.Current.StartInteraction("UploadFile");
        try
        {
            // TODO: add filter to excel files
            var file = await FilePicker.PickAsync();

            if (file is null)
            {
                await Toast.Make("Didn't select a file").Show(ct);
                return;
            }
        
            var url = await webClient.GetUploadUrlAsync(authnService.AccessToken, ct);
            
            await using var stream = await file.OpenReadAsync();
            using var client = new HttpClient(CrossNewRelic.Current.GetHttpMessageHandler());
            using var content = new MultipartFormDataContent();
            content.Add(new StreamContent(stream));

            await client.PutAsync(url.Uri, content, ct);
            await webClient.ValidateFileUploadAsync(authnService.AccessToken, url.FileId, ct);
        }
        catch (Exception e)
        {
            CrossNewRelic.Current.RecordException(e);
        }
        finally
        {
            CrossNewRelic.Current.RecordMetric("Upload Attempt", "File");
            CrossNewRelic.Current.EndInteraction(interactionId);
            await LoadFilesAsync(ct);
        }
    }
}