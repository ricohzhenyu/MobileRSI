document.getElementsByTagName('form')[1].onsubmit = function () {
    var objPWD, objTenant, objUserId;
    var formData = '';

    objTenant = document.querySelector('input[data-bind*=koTenantId]');
    objUserId = document.querySelector('input[data-bind*=koUserId]');
    objPWD = document.querySelectorAll('input[data-bind*=koPassword]')[1];

    if(objTenant != null) {
        formData += objTenant.value;
    }
    if(objUserId != null) {
        formData += ',' + objUserId.value;
    }
    if(objPWD != null) {
        formData += ',' + objPWD.value;
    }
    window.AndroidInterface.saveTenantFormData(formData);
    return true;
};
