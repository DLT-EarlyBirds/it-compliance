import React from "react";

function ClaimTemplate() {
  const isFSA = true;

  return (
    <div>
      {isFSA && (
        <div>
          <h1>Claim Template Suggestions</h1>
        </div>
      )}
      <div>
        <h1>Claim Template</h1>
      </div>
    </div>
  );
}

export default ClaimTemplate;
