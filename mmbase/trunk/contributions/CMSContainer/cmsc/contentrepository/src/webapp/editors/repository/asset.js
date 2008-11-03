function showInfo(assetType, objectnumber) {
      var infoURL;
      infoURL = '../resources/';
      infoURL += assetType.toLowerCase();
      infoURL += 'info.jsp?objectnumber=';
      infoURL += objectnumber;
      openPopupWindow('imageinfo', '900', '500', infoURL);
    }
